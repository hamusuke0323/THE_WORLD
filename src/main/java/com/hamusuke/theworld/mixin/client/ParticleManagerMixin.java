package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Shadow
    protected World world;

    @ModifyArg(method = "renderLitParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;renderParticle(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V"), index = 2)
    private float renderLitParticle(float partialTicks) {
        if (WorldInvoker.stopping(this.world)) {
            return 1.0F;
        }

        return partialTicks;
    }

    @ModifyArg(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;renderParticle(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;FFFFFF)V"), index = 2)
    private float renderParticle(float partialTicks) {
        if (WorldInvoker.stopping(this.world)) {
            return 1.0F;
        }

        return partialTicks;
    }
}
