package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.EntityLivingInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBaseMixin implements EntityLivingInvoker {
    @Shadow
    public abstract PathNavigate getNavigator();

    @Shadow
    public abstract EntityMoveHelper getMoveHelper();

    @Shadow
    protected EntityMoveHelper moveHelper;

    @Shadow
    protected abstract void updateLeashedState();

    @Redirect(method = "processInitialInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLiving;canBeLeashedTo(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean canBeLeashedTo(EntityLiving instance, EntityPlayer player) {
        if (WorldInvoker.stopping(instance.world)) {
            return true;
        }

        return instance.canBeLeashedTo(player);
    }

    @Inject(method = "updateEntityActionState", at = @At("HEAD"), cancellable = true)
    private void updateEntityActionState(CallbackInfo ci) {
        if (WorldInvoker.stopping(this.world)) {
            ++this.idleTime;
            if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) this.getRidingEntity();
                entityliving.getNavigator().setPath(this.getNavigator().getPath(), 1.5D);
                entityliving.getMoveHelper().read(this.getMoveHelper());
            }

            this.world.profiler.startSection("controls");
            this.world.profiler.startSection("move");
            this.moveHelper.onUpdateMoveHelper();
            this.world.profiler.endSection();
            this.world.profiler.endSection();
            ci.cancel();
        }
    }

    @Override
    public void updateLeashedStateV() {
        this.updateLeashedState();
    }
}
