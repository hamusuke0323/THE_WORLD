package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends EntityMixin {
    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @Inject(method = "shoot", at = @At("RETURN"))
    private void shoot(double p_37266_, double p_37267_, double p_37268_, float p_37269_, float p_37270_, CallbackInfo ci) {
        if (this.getOwner() instanceof Mob mob && mob.getTarget() instanceof Player player) {
            var invoker = PlayerInvoker.invoker(player);
            if (!invoker.shouldUseLastTheWorldPos()) {
                return;
            }

            var lastPos = invoker.getLastTheWorldPos();
            var cur = player.getEyePosition();
            var sub = lastPos.subtract(cur);
            var vec = new Vec3(p_37266_, p_37267_, p_37268_);
            vec = vec.add(sub).normalize().add(this.random.triangle(0.0, 0.0172275 * (double) p_37270_), this.random.triangle(0.0, 0.0172275 * (double) p_37270_), this.random.triangle(0.0, 0.0172275 * (double) p_37270_)).scale(p_37269_);

            this.setDeltaMovement(vec);
            double d0 = vec.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec.x, vec.z) * 57.2957763671875));
            this.setXRot((float) (Mth.atan2(vec.y, d0) * 57.2957763671875));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }
}
