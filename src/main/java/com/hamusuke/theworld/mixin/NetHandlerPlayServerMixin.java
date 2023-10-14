package com.hamusuke.theworld.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {
    @Shadow
    public EntityPlayerMP player;

    @Shadow
    @Final
    private MinecraftServer serverController;

    @Inject(method = "processUseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"), cancellable = true)
    private void processUseEntity(CPacketUseEntity packetIn, CallbackInfo ci) {
        Entity entity = packetIn.getEntityFromWorld(this.serverController.getWorld(this.player.dimension));
        this.player.attackTargetEntityWithCurrentItem(entity);
        ci.cancel();
    }
}
