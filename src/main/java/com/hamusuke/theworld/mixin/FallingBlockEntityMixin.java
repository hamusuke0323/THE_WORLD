package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends EntityMixin {
    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    private void canBeAttackedWithItem(CallbackInfoReturnable<Boolean> cir) {
        if (LevelInvoker.stopping(this.level())) {
            cir.setReturnValue(true);
        }
    }
}
