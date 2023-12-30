package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.WorldClientInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {
    private static long getSystemTime() {
        return ((WorldClientInvoker) Minecraft.getMinecraft().world).getSystemTime();
    }

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 0), index = 0)
    private float renderEffect$translate$f(float x) {
        if (WorldInvoker.stopping(Minecraft.getMinecraft().world)) {
            return (float) (getSystemTime() % 3000L) / 3000.0F / 8.0F;
        }

        return x;
    }

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 1), index = 0)
    private float renderEffect$translate$f1(float x) {
        if (WorldInvoker.stopping(Minecraft.getMinecraft().world)) {
            return (float) (getSystemTime() % 4873L) / 4873.0F / 8.0F;
        }
        return x;
    }
}
