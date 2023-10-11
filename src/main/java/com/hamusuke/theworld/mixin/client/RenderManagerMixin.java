package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.THE_WORLDUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderManager.class)
public abstract class RenderManagerMixin {
    @Shadow
    public World world;

    @ModifyVariable(method = "renderEntityStatic", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float renderEntityStatic(float partialTicks, Entity entityIn) {
        if (!THE_WORLDUtil.updatableInStoppedTime(this.world, entityIn)) {
            return 1.0F;
        }

        return partialTicks;
    }
}
