package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.EntityArrowInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityArrow.class)
public abstract class EntityArrowMixin extends EntityMixin implements EntityArrowInvoker {
    private boolean wasTimeStopping;

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    private void init(World worldIn, CallbackInfo ci) {
        this.wasTimeStopping = WorldInvoker.stopping(worldIn);
    }

    @Inject(method = "canBeAttackedWithItem", at = @At("HEAD"), cancellable = true)
    private void canBeAttackedWithItem(CallbackInfoReturnable<Boolean> cir) {
        if (WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"))
    private void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        compound.setBoolean("wasTimeStopping", this.wasTimeStopping);
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"))
    private void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        this.wasTimeStopping = compound.getBoolean("wasTimeStopping");
    }

    @Override
    public boolean wasTimeStopping() {
        return this.wasTimeStopping;
    }
}
