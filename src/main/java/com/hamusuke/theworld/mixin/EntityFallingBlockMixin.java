package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.item.EntityFallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFallingBlock.class)
public abstract class EntityFallingBlockMixin extends EntityMixin {
    @Inject(method = "canBeAttackedWithItem", at = @At("HEAD"), cancellable = true)
    private void canBeAttackedWithItem(CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(true);
        }
    }
}
