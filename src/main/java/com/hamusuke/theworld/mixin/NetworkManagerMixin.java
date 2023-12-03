package com.hamusuke.theworld.mixin;

import com.google.common.collect.Queues;
import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.invoker.WorldInvoker;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin {
    @Unique
    private final Queue<Tuple<Packet<?>, GenericFutureListener<? extends Future<? super Void>>[]>> queue = Queues.newConcurrentLinkedQueue();
    @Shadow
    private INetHandler packetListener;
    @Shadow
    private Channel channel;
    @Shadow
    @Final
    private ReentrantReadWriteLock readWriteLock;

    @Inject(method = "dispatchPacket", at = @At("HEAD"), cancellable = true)
    private void dispatchPacket(Packet<?> inPacket, GenericFutureListener<? extends Future<? super Void>>[] futureListeners, CallbackInfo ci) {
        if (!(this.packetListener instanceof NetHandlerPlayServer)) {
            return;
        }

        NetHandlerPlayServer listener = (NetHandlerPlayServer) this.packetListener;
        if (THE_WORLDUtil.shouldBeSent(listener.player, inPacket) || !WorldInvoker.stopping(listener.player.world) || listener.player.equals(WorldInvoker.invoker(listener.player.world).getStopper())) {
            return;
        }

        this.queue.add(new Tuple<>(inPacket, futureListeners));
        ci.cancel();
    }

    @Inject(method = "flushOutboundQueue", at = @At("HEAD"))
    private void flushOutboundQueue(CallbackInfo ci) {
        if (this.channel != null && this.channel.isOpen() && this.packetListener instanceof NetHandlerPlayServer && !WorldInvoker.stopping(((NetHandlerPlayServer) this.packetListener).player.world)) {
            this.readWriteLock.readLock().lock();
            try {
                while (!this.queue.isEmpty()) {
                    Tuple<Packet<?>, GenericFutureListener<? extends Future<? super Void>>[]> tuple = this.queue.poll();
                    this.channel.writeAndFlush(tuple.getFirst());
                }
            } finally {
                this.readWriteLock.readLock().unlock();
            }
        }
    }
}
