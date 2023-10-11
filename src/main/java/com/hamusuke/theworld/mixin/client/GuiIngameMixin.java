package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.MinecraftInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    @Shadow
    @Final
    protected Minecraft mc;

    @Inject(method = "renderPortal", at = @At("HEAD"), cancellable = true)
    private void renderPortalOverlay(float p_180474_1_, ScaledResolution p_180474_2_, CallbackInfo ci) {
        if (((MinecraftInvoker) this.mc).isInNPInverse()) {
            ci.cancel();
        }
    }
}
