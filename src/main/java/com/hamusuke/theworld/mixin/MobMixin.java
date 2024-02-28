package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MobInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntityMixin implements MobInvoker {
    @Shadow
    public abstract boolean isLeashed();

    @Shadow
    protected abstract void tickLeash();

    @Shadow
    protected abstract void updateControlFlags();

    @Shadow
    protected PathNavigation navigation;

    @Shadow
    protected MoveControl moveControl;

    @Shadow
    protected JumpControl jumpControl;

    @Shadow
    protected abstract void sendDebugPackets();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Shadow
    public abstract void setTarget(@org.jetbrains.annotations.Nullable LivingEntity p_21544_);

    @Redirect(method = "checkAndHandleImportantInteractions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;canBeLeashed(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean canBeLeashed(Mob instance, Player p_21418_) {
        return LevelInvoker.stopping(this.level()) && !this.isLeashed() || instance.canBeLeashed(p_21418_);
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void serverAiStep(CallbackInfo ci) {
        if (!LevelInvoker.stopping(this.level())) {
            return;
        }

        ++this.noActionTime;
        this.level().getProfiler().push("navigation");
        this.navigation.tick();
        this.level().getProfiler().pop();
        this.level().getProfiler().push("controls");
        this.level().getProfiler().push("move");
        this.moveControl.tick();
        this.level().getProfiler().popPush("jump");
        this.jumpControl.tick();
        this.level().getProfiler().pop();
        this.level().getProfiler().pop();
        this.sendDebugPackets();

        ci.cancel();
    }

    @Inject(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;tick()V", shift = Shift.BEFORE, ordinal = 0))
    private void serverAiStep$tick(CallbackInfo ci) {
        if (this.getTarget() instanceof Player player) {
            var invoker = PlayerInvoker.invoker(player);
            if (invoker.shouldUseLastTheWorldPos() && TheWorldUtil.isTargetBehind((Mob) (Object) this, player)) {
                this.setTarget(null);
            }
        }
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getX()D"))
    private double look$getX(Entity instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.shouldUseLastTheWorldPos()) {
            return invoker.getLastTheWorldPos().x;
        }

        return instance.getX();
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getZ()D"))
    private double look$getZ(Entity instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.shouldUseLastTheWorldPos()) {
            return invoker.getLastTheWorldPos().z;
        }

        return instance.getZ();
    }

    @Redirect(method = "lookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D"))
    private double look$getEyeY(LivingEntity instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.shouldUseLastTheWorldPos()) {
            return invoker.getLastTheWorldPos().y;
        }

        return instance.getEyeY();
    }

    @Inject(method = "setLeashedTo", at = @At(value = "TAIL"))
    private void setLeashedTo(Entity p_21464_, boolean p_21465_, CallbackInfo ci) {
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tickLeashV() {
        this.tickLeash();
    }

    @Override
    public void updateControlFlagsV() {
        this.updateControlFlags();
    }
}
