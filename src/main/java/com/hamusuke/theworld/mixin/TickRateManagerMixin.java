package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickRateManager.class)
public abstract class TickRateManagerMixin {
    @Inject(method = "isEntityFrozen", at = @At("RETURN"), cancellable = true)
    private void isEntityFrozen(Entity p_311574_, CallbackInfoReturnable<Boolean> cir) {
        if (LevelInvoker.stopping(p_311574_.level()) && p_311574_ instanceof Player player && TheWorldUtil.isEntityStopper(player.level(), player) && PlayerInvoker.invoker(player).isInEffect()) {
            cir.setReturnValue(true);
        }
    }
}
