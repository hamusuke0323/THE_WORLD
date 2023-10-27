package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.EntityCreature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityCreature.class)
public abstract class EntityCreatureMixin extends EntityLivingMixin {
    @ModifyConstant(method = "updateLeashedState", constant = @Constant(floatValue = 10.0F))
    private float updateLeashedState(float constant) {
        if (WorldInvoker.stopping(this.world)) {
            return 1024.0F;
        }

        return constant;
    }
}
