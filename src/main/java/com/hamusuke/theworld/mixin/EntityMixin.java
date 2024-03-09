package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Shadow
    public int invulnerableTime;

    @Shadow
    private Level level;

    @Shadow
    private Vec3 deltaMovement;

    @Shadow
    public boolean hasImpulse;

    @Unique
    protected boolean vecDirty;

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    public abstract void setSecondsOnFire(int p_20255_);

    @Shadow
    protected abstract void markHurt();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(Vec3 p_20257_);

    @Shadow
    public abstract void addDeltaMovement(Vec3 p_250128_);

    @Shadow
    public abstract void setYRot(float p_146923_);

    @Shadow
    public abstract void setXRot(float p_146927_);

    @Shadow
    public float yRotO;

    @Shadow
    public float xRotO;

    @Shadow
    public abstract float getXRot();

    @Shadow
    public abstract float getYRot();

    @Shadow
    @Final
    protected RandomSource random;

    @Inject(method = "checkFallDamage", at = @At("HEAD"), cancellable = true)
    private void checkFallDamage(double p_19911_, boolean p_19912_, BlockState p_19913_, BlockPos p_19914_, CallbackInfo ci) {
        if (LevelInvoker.stopping(this.level())) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        this.vecDirty = false;
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurt(DamageSource p_19946_, float p_19947_, CallbackInfoReturnable<Boolean> cir) {
        if (!this.level.isClientSide && p_19946_.getEntity() instanceof Player player && LevelInvoker.stopping(this.level)) {
            int i = EnchantmentHelper.getKnockbackBonus(player) + 1;
            var vec = player.getForward().scale(i);
            this.markHurt();

            if (!this.vecDirty) {
                this.vecDirty = true;
                double len = this.getDeltaMovement().length();
                this.setDeltaMovement(vec.scale(len));
            } else {
                this.addDeltaMovement(vec);
            }

            int fireAspect = EnchantmentHelper.getFireAspect(player);
            if (fireAspect > 0 && !this.isOnFire()) {
                this.setSecondsOnFire(fireAspect * 4);
            }

            if (player.getMainHandItem().getItem() == Items.BOW && (Object) this instanceof AbstractArrow arrow && !player.equals(arrow.getOwner())) {
                arrow.setOwner(player);
                arrow.pickup = Pickup.DISALLOWED;
            }

            cir.setReturnValue(true);
        }
    }

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_, CallbackInfoReturnable<InteractionResult> cir) {
        var itemStack = p_19980_.getItemInHand(p_19982_);
        if (itemStack.getItem() != Items.BOW && LevelInvoker.stopping(this.level) && (Object) this instanceof Projectile) {
            this.deltaMovement = this.deltaMovement.reverse();
            this.hasImpulse = true;

            p_19980_.swing(p_19982_);

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void playSound(SoundEvent p_19938_, float p_19939_, float p_19940_, CallbackInfo ci) {
        if (this.level().tickRateManager().isEntityFrozen((Entity) (Object) this) && !((Object) this instanceof Player)) {
            ci.cancel();
        }
    }

    @Inject(method = "setAirSupply", at = @At("HEAD"), cancellable = true)
    private void setAirSupply(int air, CallbackInfo ci) {
        if (LevelInvoker.stopping(this.level)) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
    private void setRemainingFireTicks(int p_70015_1_, CallbackInfo ci) {
        if (LevelInvoker.stopping(this.level) && p_70015_1_ > 0) {
            ci.cancel();
        }
    }

    @Inject(method = "setTicksFrozen", at = @At("HEAD"), cancellable = true)
    private void setTicksFrozen(int p_146918_, CallbackInfo ci) {
        if (LevelInvoker.stopping(this.level) && p_146918_ > 0) {
            ci.cancel();
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.CRAMMING) || (Object) this instanceof Player && LevelInvoker.stopping(this.level) && (source.getEntity() == null || this.level.tickRateManager().isEntityFrozen(source.getEntity()))) {
            cir.setReturnValue(true);
        }
    }
}
