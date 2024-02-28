package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.AbstractArrowInvoker;
import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends EntityMixin implements AbstractArrowInvoker {
    @Unique
    protected boolean wasTimeStopping;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void init(EntityType<?> p_36717_, Level p_36719_, ItemStack p_312639_, CallbackInfo ci) {
        this.wasTimeStopping = LevelInvoker.stopping(p_36719_);
    }

    @Inject(method = "isAttackable", at = @At("RETURN"), cancellable = true)
    private void isAttackable(CallbackInfoReturnable<Boolean> cir) {
        if (LevelInvoker.stopping(this.level())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalSaveData(CompoundTag p_36772_, CallbackInfo ci) {
        p_36772_.putBoolean("wasTimeStopping", this.wasTimeStopping);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalSaveData(CompoundTag p_36761_, CallbackInfo ci) {
        this.wasTimeStopping = p_36761_.getBoolean("wasTimeStopping");
    }

    @Override
    public boolean wasTimeStopping() {
        return this.wasTimeStopping;
    }
}
