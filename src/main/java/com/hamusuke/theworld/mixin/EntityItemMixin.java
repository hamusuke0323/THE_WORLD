package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends EntityMixin {
    @Shadow
    private int pickupDelay;

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onUpdate(CallbackInfo ci) {
        if (getItem().getItem().onEntityItemUpdate((EntityItem) (Object) this)) {
            ci.cancel();
            return;
        }

        if (!this.getItem().isEmpty() && WorldInvoker.stopping(this.world)) {
            super.onUpdate();
            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }
            ci.cancel();
        }
    }
}
