package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LookControl.class)
public abstract class LookControlMixin {
    @Shadow
    @Final
    protected Mob mob;

    @Shadow
    protected int lookAtCooldown;

    @Shadow
    protected float xMaxRotAngle;

    @Shadow
    protected float yMaxRotSpeed;

    @Shadow
    protected double wantedX;

    @Shadow
    protected double wantedY;

    @Shadow
    protected double wantedZ;

    @Inject(method = "setLookAt(DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void setLookAt(double p_24951_, double p_24952_, double p_24953_, float p_24954_, float p_24955_, CallbackInfo ci) {
        if (this.mob.getTarget() instanceof PlayerInvoker invoker && invoker.shouldUseLastTheWorldPos()) {
            this.wantedX = invoker.getLastTheWorldPos().x;
            this.wantedY = invoker.getLastTheWorldPos().y;
            this.wantedZ = invoker.getLastTheWorldPos().z;
            this.yMaxRotSpeed = p_24954_;
            this.xMaxRotAngle = p_24955_;
            this.lookAtCooldown = 2;
            ci.cancel();
        }
    }
}
