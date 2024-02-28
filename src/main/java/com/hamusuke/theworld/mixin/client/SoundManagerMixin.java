package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {
    @Inject(method = "resume", at = @At("HEAD"), cancellable = true)
    private void resume(CallbackInfo ci) {
        if (LevelInvoker.stopping(Minecraft.getInstance().level)) {
            ci.cancel();
        }
    }
}
