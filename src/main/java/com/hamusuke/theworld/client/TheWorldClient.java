package com.hamusuke.theworld.client;

import com.hamusuke.theworld.TheWorld;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.c2s.ReleaseLeashedEntitiesReq;
import com.hamusuke.theworld.network.packet.c2s.ReleaseTheWorldReq;
import com.hamusuke.theworld.network.packet.c2s.TheWorldReq;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

import static com.hamusuke.theworld.TheWorldUtil.THE_WORLD_EFFECT_TICK;

@OnlyIn(Dist.CLIENT)
public final class TheWorldClient {
    public static final KeyMapping THE_WORLD_KEY = new KeyMapping(TheWorld.MOD_ID + ".key.THE_WORLD", 47, TheWorld.MOD_ID + ".key.category");
    public static final KeyMapping RELEASE_LEASHED_KEY = new KeyMapping(TheWorld.MOD_ID + ".key.release.leashed", 48, TheWorld.MOD_ID + ".key.category");
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation THE_WORLD_NP_INV = new ResourceLocation(TheWorld.MOD_ID, "textures/the_world_negaposiinv.png");
    private static TheWorldClient INSTANCE;
    public static final ResourceLocation THE_WORLD_GRAYSCALE_SHADER = new ResourceLocation(TheWorld.MOD_ID, "shaders/post/grayscale.json");

    private TheWorldClient() {
        INSTANCE = this;
    }

    public static TheWorldClient getInstance() {
        if (INSTANCE == null) {
            new TheWorldClient();
        }

        return INSTANCE;
    }

    @SubscribeEvent
    public void onPostTick(final ClientTickEvent event) {
        if (event.phase == Phase.END) {
            while (THE_WORLD_KEY.consumeClick()) {
                if (!LevelInvoker.stopping(mc.level)) {
                    NetworkManager.sendToServer(new TheWorldReq());
                } else if (!MinecraftInvoker.invoker(mc).isInNPInverse()) {
                    NetworkManager.sendToServer(new ReleaseTheWorldReq());
                }
            }

            while (RELEASE_LEASHED_KEY.consumeClick()) {
                NetworkManager.sendToServer(new ReleaseLeashedEntitiesReq());
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(final RenderGuiOverlayEvent.Post event) {
        var invoker = MinecraftInvoker.invoker(mc);
        if (invoker.isInNPInverse()) {
            var stack = event.getGuiGraphics().pose();
            stack.pushPose();
            float prev = Mth.clamp(invoker.getInverseTick() + 1, 0, THE_WORLD_EFFECT_TICK);
            float f = Mth.clampedLerp(prev / (THE_WORLD_EFFECT_TICK / 2.0F), invoker.getInverseTick() / (THE_WORLD_EFFECT_TICK / 2.0F), event.getPartialTick());
            if (invoker.getInverseTick() > THE_WORLD_EFFECT_TICK / 2) {
                f = 2.0F - f;
            }

            Objects.requireNonNull(mc.player).spinningEffectIntensity = (float) invoker.getInverseTick() / THE_WORLD_EFFECT_TICK;
            f *= 5.0F;
            int i = event.getWindow().getGuiScaledWidth();
            int j = event.getWindow().getGuiScaledHeight();
            stack.translate((float) i / 2.0F, (float) j / 2.0F, 0.0F);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
            stack.scale(f, f, f);
            RenderSystem.setShaderTexture(0, THE_WORLD_NP_INV);
            event.getGuiGraphics().blit(THE_WORLD_NP_INV, -i / 2, -j / 2, 0.0F, 0.0F, i, j, 16, 16);
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            stack.popPose();
        }
    }
}
