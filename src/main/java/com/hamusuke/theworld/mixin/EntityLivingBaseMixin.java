package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.EntityLivingBaseInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends EntityMixin implements EntityLivingBaseInvoker {
    @Shadow
    protected abstract void collideWithNearbyEntities();

    @Shadow
    public abstract boolean isEntityAlive();

    @Shadow
    protected int idleTime;

    @Shadow
    protected abstract boolean isPlayer();

    @Shadow
    protected int recentlyHit;
    @Shadow
    protected abstract int getExperiencePoints(EntityPlayer player);

    @Shadow
    protected EntityPlayer attackingPlayer;

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 0.0F, ordinal = 0))
    private float attackEntityFrom(float constant) {
        return -1.0F;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world)) {
            this.hurtResistantTime = 0;
        }
    }

    @Inject(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getExperiencePoints(Lnet/minecraft/entity/player/EntityPlayer;)I", shift = At.Shift.BEFORE), cancellable = true)
    private void onDeathUpdate$getExperiencePointsI(CallbackInfo ci) {
        this.setDead();

        for (int k = 0; k < 20; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d2, d0, d1);
        }
        ci.cancel();
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;dropLoot(ZILnet/minecraft/util/DamageSource;)V", shift = At.Shift.AFTER))
    private void onDeath$dropLootV(DamageSource cause, CallbackInfo ci) {
        if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0)) {
            int i = this.getExperiencePoints(this.attackingPlayer);
            i = ForgeEventFactory.getExperienceDrop((EntityLivingBase) (Object) this, this.attackingPlayer, i);
            while (i > 0) {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }
        }
    }

    @Inject(method = "knockBack", at = @At("HEAD"), cancellable = true)
    private void knockBack(Entity entityIn, float strength, double xRatio, double zRatio, CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world) && !this.isEntityAlive()) {
            this.addVelocity(-xRatio * strength * 0.01D, 0.0D, -zRatio * strength * 0.01D);
            ci.cancel();
        }
    }

    @Override
    public void collideWithNearbyEntitiesV() {
        this.collideWithNearbyEntities();
    }
}
