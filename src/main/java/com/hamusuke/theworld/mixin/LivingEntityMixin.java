package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldConfig;
import com.hamusuke.theworld.invoker.AbstractArrowInvoker;
import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow
    protected int noActionTime;

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z", ordinal = 0))
    private boolean hurt$isDeadOrDying$0(LivingEntity instance) {
        return !LevelInvoker.stopping(this.level()) && instance.isDeadOrDying();
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurt(DamageSource p_21016_, float p_21017_, CallbackInfoReturnable<Boolean> cir) {
        if (!LevelInvoker.stopping(this.level()) && !p_21016_.is(DamageTypes.CRAMMING) && !p_21016_.is(DamageTypes.FALL) && !p_21016_.is(DamageTypes.OUTSIDE_BORDER) && !p_21016_.is(DamageTypes.WITHER) && !p_21016_.is(DamageTypes.STARVE) && !p_21016_.is(DamageTypes.MAGIC) && !p_21016_.is(DamageTypes.THORNS) && !p_21016_.is(DamageTypes.FELL_OUT_OF_WORLD) && (Object) this instanceof Player player && !this.level().isClientSide && TheWorldConfig.autoTheWorld) {
            LevelInvoker.invoker(this.level()).stopTime(player);
            cir.setReturnValue(false);
        }

        if (this.level().tickRateManager().isEntityFrozen((Entity) (Object) this) || (p_21016_.getDirectEntity() instanceof AbstractArrow arrow && AbstractArrowInvoker.invoker(arrow).wasTimeStopping())) {
            this.invulnerableTime = 0;
        }
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private void hurt$knockback(LivingEntity instance, double strength, double x, double z) {
        if (!LevelInvoker.stopping(this.level())) {
            instance.knockback(strength, x, z);
            return;
        }

        var attacker = instance.getKillCredit();
        if (attacker == null) {
            return;
        }

        int i = EnchantmentHelper.getKnockbackBonus(instance.getKillCredit()) + 1;
        instance.addDeltaMovement(attacker.getForward().scale(i * 0.25D));
    }
}
