package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldConfig;
import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.TheWorldSuccNotify;
import com.hamusuke.theworld.network.packet.s2c.TimeOverNotify;
import com.hamusuke.theworld.network.packet.s2c.TimeStopsNotify;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelInvoker {
    @Shadow
    @Final
    public boolean isClientSide;

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    public abstract <T extends Entity> void guardEntityTick(Consumer<T> p_46654_, T p_46655_);

    @Unique
    protected boolean timeStopping;
    @Unique
    protected int timeLimitTicks;
    @Unique
    protected Player stopper;

    @Override
    public void stopTime(Player stopper) {
        if (this.timeStopping || stopper == null) {
            return;
        }

        if (!this.isClientSide && !stopper.isCreative() && !PlayerInvoker.invoker(stopper).canDeclareTheWorld()) {
            return;
        }

        this.stopper = stopper;
        this.timeStopping = true;
        PlayerInvoker.invoker(this.stopper).setLastTheWorldPos(this.stopper.getEyePosition());
        if (!this.isClientSide) {
            NetworkManager.sendToClient(new TheWorldSuccNotify(), (ServerPlayer) this.stopper);
            if (TheWorldConfig.allowFlyWhenTimeStopping && !this.stopper.getAbilities().mayfly) {
                this.stopper.getAbilities().mayfly = true;

                if (!this.stopper.onGround()) {
                    this.stopper.getAbilities().flying = true;
                    this.stopper.setDeltaMovement(Vec3.ZERO);
                    this.stopper.hasImpulse = true;
                }

                this.stopper.onUpdateAbilities();
            }
            NetworkManager.sendToDimension(new TimeStopsNotify(this.stopper), (ServerPlayer) this.stopper);
            var manager = Objects.requireNonNull(this.getServer()).tickRateManager();
            if (manager.isSprinting()) {
                manager.stopSprinting();
            }

            if (manager.isSteppingForward()) {
                manager.stopStepping();
            }

            manager.setFrozen(true);
        }
    }

    @Override
    public void startTime(Player releaser) {
        if (!this.timeStopping || !TheWorldUtil.isEntityStopper(this, releaser)) {
            return;
        }

        var stopper = PlayerInvoker.invoker(this.stopper);
        this.timeStopping = false;
        this.stopper.fallDistance = 0.0F;
        stopper.setLastTheWorldPosExpiresTicks(TheWorldUtil.LAST_THE_WORLD_POS_EXPIRES_TICKS);
        if (!this.isClientSide) {
            ((ServerPlayer) this.stopper).gameMode.getGameModeForPlayer().updatePlayerAbilities(this.stopper.getAbilities());
            this.stopper.onUpdateAbilities();
            NetworkManager.sendToDimension(new TimeOverNotify(), (ServerPlayer) this.stopper);
            stopper.setCoolDownTicks(TheWorldUtil.getAdjustedCoolDown(this.timeLimitTicks));
            stopper.setLastTheWorldPosExpiresTicks(TheWorldUtil.LAST_THE_WORLD_POS_EXPIRES_TICKS);

            Objects.requireNonNull(this.getServer()).tickRateManager().setFrozen(false);
        }
    }

    @Override
    public boolean timeStopping() {
        return this.timeStopping;
    }

    @Override
    public Player getStopper() {
        return this.stopper;
    }

    @Override
    public void setTimeLimitTicks(int ticks) {
        this.timeLimitTicks = ticks;
    }
}
