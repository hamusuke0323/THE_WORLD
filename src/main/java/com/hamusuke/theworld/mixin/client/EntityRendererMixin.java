package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.EntityRendererInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements EntityRendererInvoker {
	@Shadow
	@Final
	private Minecraft mc;

	@Shadow
	private int rendererUpdateCount;

	@ModifyVariable(method = "renderRainSnow", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float renderRainSnow(float partialTicks) {
		if (WorldInvoker.stopping(this.mc.world)) {
			return 1.0F;
		}

		return partialTicks;
	}

	@Redirect(method = "updateRenderer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;rendererUpdateCount:I", opcode = Opcodes.PUTFIELD))
	private void updateRenderer(EntityRenderer instance, int value) {
		if (!WorldInvoker.stopping(this.mc.world)) {
			((EntityRendererInvoker) instance).setRendererUpdateCount(value);
		}
	}

	@Override
	public void setRendererUpdateCount(int rendererUpdateCount) {
		this.rendererUpdateCount = rendererUpdateCount;
	}
}
