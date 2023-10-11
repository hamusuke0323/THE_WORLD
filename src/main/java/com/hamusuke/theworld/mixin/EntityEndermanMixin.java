package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.monster.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderman.class)
public abstract class EntityEndermanMixin extends EntityMixin {
    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true)
    private void teleportTo(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(false);
        }
    }
}
