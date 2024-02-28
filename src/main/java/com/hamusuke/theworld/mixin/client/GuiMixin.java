package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.MinecraftInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPortalOverlay(GuiGraphics p_283375_, float p_283296_, CallbackInfo ci) {
        if (((MinecraftInvoker) this.minecraft).isInNPInverse()) {
            ci.cancel();
        }
    }
}
