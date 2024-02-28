package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldConfig;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.SetIsInEffectFlagNotify;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends EntityMixin implements PlayerInvoker {
    @Unique
    private boolean loggedIn;
    @Unique
    private boolean isInEffect;
    @Unique
    private int coolDownTicks;
    @Unique
    private int lastTheWorldPosExpiresTicks;
    @Unique
    private Vec3 lastTheWorldPos;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.coolDownTicks > 0) {
            --this.coolDownTicks;
        }

        if (this.lastTheWorldPosExpiresTicks > 0) {
            --this.lastTheWorldPosExpiresTicks;
        }
    }

    @Inject(method = "getAttackStrengthScale", at = @At("RETURN"), cancellable = true)
    private void getAttackStrengthScale(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        if (LevelInvoker.stopping(this.level())) {
            cir.setReturnValue(1.0F); // TOO FAST SO THAT TIME SEEMS TO STOP RELATIVELY...?
        }
    }

    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public void setIsInEffect(boolean flag) {
        this.isInEffect = flag;
        if (!this.level().isClientSide()) {
            if (!flag) {
                ((LevelInvoker) this.level()).setTimeLimitTicks(TheWorldConfig.timeLimitTicks);
            }
            NetworkManager.sendToDimension(new SetIsInEffectFlagNotify((ServerPlayer) (Object) this), (ServerPlayer) (Object) this);
        }
    }

    @Override
    public boolean isInEffect() {
        return this.isInEffect;
    }

    @Override
    public int getCoolDownTicks() {
        return this.coolDownTicks;
    }

    @Override
    public void setCoolDownTicks(int coolDownTicks) {
        this.coolDownTicks = coolDownTicks;
    }

    @Override
    public Vec3 getLastTheWorldPos() {
        return this.lastTheWorldPos == null ? Vec3.ZERO : this.lastTheWorldPos;
    }

    @Override
    public void setLastTheWorldPos(Vec3 lastTheWorldPos) {
        this.lastTheWorldPos = lastTheWorldPos;
    }

    @Override
    public int getLastTheWorldPosExpiresTicks() {
        return this.lastTheWorldPosExpiresTicks;
    }

    @Override
    public void setLastTheWorldPosExpiresTicks(int lastTheWorldPosExpiresTicks) {
        this.lastTheWorldPosExpiresTicks = lastTheWorldPosExpiresTicks;
    }
}
