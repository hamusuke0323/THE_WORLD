package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectPacket;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityMixin implements EntityPlayerInvoker {
    @Unique
    private boolean isInEffect;
    @Unique
    private int coolDownTicks;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        if (this.coolDownTicks > 0) {
            --this.coolDownTicks;
        }
    }

    @Inject(method = "getCooledAttackStrength", at = @At("RETURN"), cancellable = true)
    private void getCooledAttackStrength(float adjustTicks, CallbackInfoReturnable<Float> cir) {
        if (WorldInvoker.stopping(this.world)) {
            cir.setReturnValue(1.0F); // TOO FAST SO THAT TIME SEEMS TO STOP RELATIVELY...?
        }
    }

    @Override
    public void setIsInEffect(boolean flag) {
        this.isInEffect = flag;
        if (!this.world.isRemote) {
            if (!flag) {
                ((WorldInvoker) this.world).setTimeLimitTicks(CommonConfig.timeLimitTicks);
            }
            NetworkManager.sendToDimension(new PlayerSetIsInEffectPacket((EntityPlayer) (Object) this), this.world.provider.getDimension());
        }
    }

    @Override
    public boolean isInEffect() {
        return this.isInEffect;
    }

    @Override
    public int getCoolDownTicks() {
        return this.coolDownTicks;
    }

    @Override
    public void setCoolDownTicks(int coolDownTicks) {
        this.coolDownTicks = coolDownTicks;
    }
}
