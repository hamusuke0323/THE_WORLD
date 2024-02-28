package com.hamusuke.theworld;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class TheWorldUtil {
    public static final int THE_WORLD_EFFECT_TICK = 40;
    public static final int LAST_THE_WORLD_POS_EXPIRES_TICKS = 15;

    public static boolean isTargetBehind(Entity self, Entity target) {
        double subX = target.getX() - self.getX();
        double subZ = target.getZ() - self.getZ();
        double subY;
        if (target instanceof LivingEntity livingentity) {
            subY = livingentity.getEyeY() - self.getEyeY();
        } else {
            subY = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0 - self.getEyeY();
        }

        double distance = Math.sqrt(subX * subX + subZ * subZ);
        float yaw = (float) (Mth.atan2(subZ, subX) * 57.2957763671875 - 90.0F);
        float pitch = (float) -(Mth.atan2(subY, distance) * 57.2957763671875);

        return Mth.degreesDifferenceAbs(self.getYRot(), yaw) >= 100.0F || Mth.degreesDifferenceAbs(self.getXRot(), pitch) >= 60.0F;
    }

    public static boolean isEntityStopper(Object world, @Nullable Entity entity) {
        return entity != null && LevelInvoker.stopping(world) && entity.equals(LevelInvoker.invoker(world).getStopper());
    }

    public static int getAdjustedCoolDown(int curTimeOverTicks) {
        int max = TheWorldConfig.timeLimitTicks;
        return Math.max((int) (TheWorldConfig.maxCoolDownTicks * (max - curTimeOverTicks) / (float) max), 0);
    }

    public static boolean shouldBeSent(ServerPlayer serverPlayer, Packet<?> packet) {
        return !PlayerInvoker.invoker(serverPlayer).isLoggedIn() || packet instanceof ClientboundForgetLevelChunkPacket || packet instanceof ClientboundCommandSuggestionsPacket || packet instanceof ClientboundPlayerInfoUpdatePacket || packet instanceof ClientboundPlayerInfoRemovePacket || packet instanceof ClientboundCustomPayloadPacket || packet instanceof ClientboundCustomChatCompletionsPacket || packet instanceof ClientboundSystemChatPacket || packet instanceof ClientboundKeepAlivePacket || packet instanceof ClientboundAddEntityPacket || packet instanceof ClientboundDisconnectPacket;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean amIStopper(Entity stopper) {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.equals(stopper);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playTheWorldSE(Player stopper, ResourceLocation resourceLocation) {
        var mc = Minecraft.getInstance();
        if (amIStopper(stopper)) {
            mc.tell(() -> mc.getSoundManager().play(new SimpleSoundInstance(resourceLocation, SoundSource.PLAYERS, 1.0F, 1.0F, SoundInstance.createUnseededRandom(), false, 0, Attenuation.LINEAR, 0.0D, 0.0D, 0.0D, true)));
        }
    }
}
