package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.SoundHandlerInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundHandler.class)
public abstract class SoundHandlerMixin implements SoundHandlerInvoker {
    @Shadow
    @Final
    private SoundManager sndManager;

    @Inject(method = "resumeSounds", at = @At("HEAD"), cancellable = true)
    private void resumeSounds(CallbackInfo ci) {
        if (WorldInvoker.stopping(Minecraft.getMinecraft().world)) {
            ci.cancel();
        }
    }

    @Override
    public SoundManager getSoundManager() {
        return this.sndManager;
    }
}
