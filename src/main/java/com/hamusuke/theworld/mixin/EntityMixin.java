package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "canBeCollidedWith", at = @At("HEAD"), cancellable = true)
    private void canBeCollidedWith(CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getTrueSource() instanceof EntityPlayer && WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(true);
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
        if (WorldInvoker.stopping(this.world) && p_70015_1_ > 0) {
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
