package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public World world;

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    public int hurtResistantTime;

    @Shadow
    public abstract void addVelocity(double x, double y, double z);

    @Shadow
    public abstract boolean isRiding();

    @Shadow
    @Nullable
    public abstract Entity getRidingEntity();

    @Shadow
    public abstract void setDead();

    @Shadow
    protected Random rand;

    @Shadow
    public double posX;

    @Shadow
    public float width;

    @Shadow
    public double posY;

    @Shadow
    public float height;

    @Shadow
    public double posZ;

    @Shadow
    public void onUpdate() {
    }

    @Shadow
    public double motionX;

    @Shadow
    public double motionY;

    @Shadow
    public double motionZ;

    @Unique
    protected boolean alreadyVelocityChanged;

    @Shadow
    protected abstract void markVelocityChanged();

    @Shadow
    public abstract boolean isBurning();

    @Shadow
    public abstract void setFire(int seconds);

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        this.alreadyVelocityChanged = false;
    }

    @Inject(method = "canBeCollidedWith", at = @At("HEAD"), cancellable = true)
    private void canBeCollidedWith(CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world) && !((Object) this instanceof EntityItem)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateFallState", at = @At("HEAD"), cancellable = true)
    private void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos, CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world)) {
            ci.cancel();
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.world.isRemote && source.getTrueSource() instanceof EntityPlayer && WorldInvoker.stopping(this.world)) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            int i = EnchantmentHelper.getKnockbackModifier(player) + 1;
            Vec3d vec3d = player.getLookVec();
            this.markVelocityChanged();

            double x = vec3d.x * i;
            double y = vec3d.y * i;
            double z = vec3d.z * i;

            if (!this.alreadyVelocityChanged) {
                this.alreadyVelocityChanged = true;
                double vecLen = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.motionX = x * vecLen;
                this.motionY = y * vecLen;
                this.motionZ = z * vecLen;
            } else {
                this.addVelocity(x, y, z);
            }

            int fireAspect = EnchantmentHelper.getFireAspectModifier(player);
            if (fireAspect > 0 && !this.isBurning()) {
                this.setFire(fireAspect * 4);
            }

            if ((Object) this instanceof EntityArrow && !player.equals(((EntityArrow) (Object) this).shootingEntity)) {
                ((EntityArrow) (Object) this).shootingEntity = player;
            }

            cir.setReturnValue(true);
        }
    }

    @Inject(method = "applyPlayerInteraction", at = @At("HEAD"), cancellable = true)
    private void applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
        if (WorldInvoker.stopping(this.world) && this instanceof IProjectile) {
            this.motionX *= -1.0F;
            this.motionY *= -1.0F;
            this.motionZ *= -1.0F;

            player.swingArm(hand);

            cir.setReturnValue(EnumActionResult.SUCCESS);
        }
    }

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_, CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world) && !((Object) this instanceof EntityPlayer)) {
            ci.cancel();
        }
    }

    @Inject(method = "setAir", at = @At("HEAD"), cancellable = true)
    private void setAir(int air, CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world)) {
            ci.cancel();
        }
    }

    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true)
    private void setFire(int p_70015_1_, CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world) && (Object) this instanceof EntityLivingBase && p_70015_1_ > 0) {
            ci.cancel();
        }
    }

    @Inject(method = "isEntityInvulnerable", at = @At("HEAD"), cancellable = true)
    private void isEntityInvulnerable(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source == DamageSource.CRAMMING || (Object) this instanceof EntityPlayer && WorldInvoker.stopping(this.world) && (source.getTrueSource() == null || !THE_WORLDUtil.updatableInStoppedTime(this.world, source.getTrueSource()))) {
            cir.setReturnValue(true);
        }
    }
}
