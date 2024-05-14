package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.client.TheWorldClient;
import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    public abstract void loadEffect(ResourceLocation p_109129_);

    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void loadEntityShader(Entity entityIn, CallbackInfo ci) {
        if (LevelInvoker.stopping(this.minecraft.level)) {
            this.loadEffect(TheWorldClient.THE_WORLD_GRAYSCALE_SHADER);
            ci.cancel();
        }
    }

    @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
    private EntityHitResult pick(Entity vec31, Vec3 d1, Vec3 aabb, AABB optional, Predicate<Entity> entity1, double p_37288_) {
        return ProjectileUtil.getEntityHitResult(vec31, d1, aabb, optional, entity -> {
            if (LevelInvoker.stopping(this.minecraft.level)) {
                return !entity.isSpectator() && entity != this.minecraft.player && !(entity instanceof ExperienceOrb) && !(entity instanceof ItemEntity);
            }

            return entity1.test(entity);
        }, p_37288_);
    }
}
