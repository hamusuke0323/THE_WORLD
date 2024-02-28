package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends EntityMixin {
    @Inject(method = "teleport(DDD)Z", at = @At("HEAD"), cancellable = true)
    private void teleportTo(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (LevelInvoker.stopping(this.level())) {
            cir.setReturnValue(false);
        }
    }
}
