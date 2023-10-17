package com.hamusuke.theworld.client;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.c2s.DeclareTHE_WORLDPacket;
import com.hamusuke.theworld.network.packet.c2s.ReleaseLeashedEntitiesRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.hamusuke.theworld.THE_WORLDUtil.THE_WORLD_EFFECT_TICK;

@SideOnly(Side.CLIENT)
public final class THE_WORLDClient {
	private static THE_WORLDClient INSTANCE;
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static final KeyBinding THE_WORLD_KEY = new KeyBinding(THE_WORLD.MOD_ID + ".key.THE_WORLD", 47, THE_WORLD.MOD_ID + ".key.category");
	public static final KeyBinding RELEASE_LEASHED_KEY = new KeyBinding(THE_WORLD.MOD_ID + ".key.release.leashed", 48, THE_WORLD.MOD_ID + ".key.category");
	private static final ResourceLocation THE_WORLD_NP_INV = new ResourceLocation(THE_WORLD.MOD_ID, "textures/the_world_negaposiinv.png");

	private THE_WORLDClient() {
		INSTANCE = this;
		ClientRegistry.registerKeyBinding(THE_WORLD_KEY);
		ClientRegistry.registerKeyBinding(RELEASE_LEASHED_KEY);
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			while (THE_WORLD_KEY.isPressed()) {
				if (!WorldInvoker.stopping(mc.world)) {
					NetworkManager.sendToServer(new DeclareTHE_WORLDPacket());
				}
			}

			while (RELEASE_LEASHED_KEY.isPressed()) {
				NetworkManager.sendToServer(new ReleaseLeashedEntitiesRequestPacket());
			}
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			MinecraftInvoker client = (MinecraftInvoker) mc;
			if (client.isInNPInverse()) {
				GlStateManager.pushMatrix();
				float prev = MathHelper.clamp(client.getInverseTick() + 1, 0, THE_WORLD_EFFECT_TICK);
				float f = (float) MathHelper.clampedLerp(prev / (THE_WORLD_EFFECT_TICK / 2.0F), client.getInverseTick() / (THE_WORLD_EFFECT_TICK / 2.0F), event.getPartialTicks());
				if (client.getInverseTick() > THE_WORLD_EFFECT_TICK / 2) {
					f = 2.0F - f;
				}
				mc.player.prevTimeInPortal = -(float) Math.random() * 2.0F;
				mc.player.timeInPortal = (float) ((Math.random() * 15.0F + f) * 0.25F);
				f *= 5.0F;
				int i = event.getResolution().getScaledWidth();
				int j = event.getResolution().getScaledHeight();
				GlStateManager.translate((float) i / 2.0F, (float) j / 2.0F, 0.0F);
				GlStateManager.disableDepth();
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableAlpha();
				GlStateManager.scale(f, f, f);
				mc.getTextureManager().bindTexture(THE_WORLD_NP_INV);
				Gui.drawModalRectWithCustomSizedTexture(-i / 2, -j / 2, 0.0F, 0.0F, i, j, 16.0F, 16.0F);
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
			}

			if (WorldInvoker.stopping(mc.world)) {
				Gui.drawRect(0, 0, event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), 80 << 24 | 16 << 16 | 16 << 8 | 16);
			}
		}
	}

	public static THE_WORLDClient getInstance() {
		if (INSTANCE == null) {
			new THE_WORLDClient();
		}

		return INSTANCE;
	}
}
