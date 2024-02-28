package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.mixin.LevelMixin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends LevelMixin {
    @Shadow
    public abstract TickRateManager tickRateManager();

    @Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    private void tickEntities$forEach(EntityTickList instance, Consumer<Entity> p_156911_) {
        instance.forEach(entity -> {
            if (LevelInvoker.stopping(this) && this.tickRateManager().isEntityFrozen(entity)) {
                if (entity instanceof Mob mob && mob.isLeashed()) {
                    mob.aiStep();
                    mob.walkAnimation.setSpeed(0.0F);
                }

                return;
            }

            p_156911_.accept(entity);
        });
    }

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    private void playSound(double p_233603_, double p_233604_, double p_233605_, SoundEvent p_233606_, SoundSource p_233607_, float p_233608_, float p_233609_, boolean p_233610_, long p_233611_, CallbackInfo ci) {
        if (this.timeStopping && p_233607_ != SoundSource.BLOCKS && p_233607_ != SoundSource.PLAYERS) {
            ci.cancel();
        }
    }
}
