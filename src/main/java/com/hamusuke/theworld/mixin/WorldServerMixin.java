package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends WorldMixin {
    @Shadow
    @Final
    private PlayerChunkMap playerChunkMap;

    @Shadow
    protected abstract void sendQueuedBlockEvents();

    @Shadow
    protected abstract void updateBlocks();

    @Shadow
    public abstract boolean tickUpdates(boolean runAllPending);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (this.timeStopping) {
            if (!((EntityPlayerInvoker) this.stopper).isInEffect()) {
                if (this.timeLimitTicks > 0) {
                    --this.timeLimitTicks;
                } else if (!this.stopper.isCreative()) {
                    this.startTime(this.stopper);
                }
            }

            this.provider.getBiomeProvider().cleanupCache();
            this.profiler.endStartSection("chunkSource");
            this.chunkProvider.tick();
            int j = this.calculateSkylightSubtracted(1.0F);

            if (j != this.getSkylightSubtracted()) {
                this.setSkylightSubtracted(j);
            }

            this.profiler.endStartSection("tickPending");
            this.tickUpdates(false);
            this.profiler.endStartSection("tickBlocks");
            this.updateBlocks();
            this.profiler.endStartSection("chunkMap");
            this.playerChunkMap.tick();
            this.profiler.endSection();
            this.sendQueuedBlockEvents();

            ci.cancel();
        }
    }
}
