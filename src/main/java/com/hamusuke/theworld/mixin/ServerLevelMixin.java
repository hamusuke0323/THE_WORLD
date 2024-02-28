package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MobInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends LevelMixin {
    @Shadow
    public abstract TickRateManager tickRateManager();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(BooleanSupplier p_8794_, CallbackInfo ci) {
        if (this.timeStopping) {
            if (!PlayerInvoker.invoker(this.stopper).isInEffect()) {
                if (this.timeLimitTicks > 0) {
                    --this.timeLimitTicks;
                } else {
                    this.startTime(this.stopper);
                }
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void tick$forEach(EntityTickList instance, Consumer<Entity> p_156911_) {
        instance.forEach(entity -> {
            if (LevelInvoker.stopping(this) && this.tickRateManager().isEntityFrozen(entity)) {
                if (entity instanceof Mob mob && mob.isLeashed()) {
                    this.guardEntityTick(mob2 -> {
                        if (!this.isClientSide) {
                            mob2.aiStep();

                            if (!this.isClientSide) {
                                MobInvoker.invoker(mob2).tickLeashV();
                                if (mob2.tickCount % 5 == 0) {
                                    MobInvoker.invoker(mob2).updateControlFlagsV();
                                }
                            }
                        }
                    }, mob);
                } else if (entity instanceof ItemEntity item && item.hasPickUpDelay()) {
                    item.setNoPickUpDelay();
                }

                return;
            }

            p_156911_.accept(entity);
        });
    }
}
