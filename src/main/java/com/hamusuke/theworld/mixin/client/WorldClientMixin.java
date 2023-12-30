package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.WorldClientInvoker;
import com.hamusuke.theworld.mixin.WorldMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends WorldMixin implements WorldClientInvoker {
    @Shadow
    protected abstract void updateBlocks();

    @Shadow
    private ChunkProviderClient clientChunkProvider;

    private long systemTime;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (this.timeStopping) {
            this.profiler.endStartSection("chunkCache");
            this.clientChunkProvider.tick();
            this.profiler.endStartSection("blocks");
            this.updateBlocks();
            this.profiler.endSection();
            ci.cancel();
        }
    }

    @Inject(method = "playSound(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V", at = @At("HEAD"), cancellable = true)
    private void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay, CallbackInfo ci) {
        if (this.timeStopping && category != SoundCategory.BLOCKS && category != SoundCategory.PLAYERS) {
            ci.cancel();
        }
    }

    @Override
    public synchronized void stopTime(EntityPlayer stopper) {
        this.systemTime = Minecraft.getSystemTime();
        super.stopTime(stopper);
    }

    @Override
    public long getSystemTime() {
        return this.systemTime;
    }
}
