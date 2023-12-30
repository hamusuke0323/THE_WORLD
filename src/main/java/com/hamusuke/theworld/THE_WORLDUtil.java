package com.hamusuke.theworld;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class THE_WORLDUtil {
    public static final int THE_WORLD_EFFECT_TICK = 40;

    public static boolean updatableInStoppedTime(Object theWORLD, Entity entity) {
        if (!WorldInvoker.stopping(theWORLD)) {
            return true;
        }

        return isEntityStopper(theWORLD, entity) || isTamedByStopper(entity) || entity.getRecursivePassengersByType(EntityPlayer.class).size() == 1 || entity instanceof EntityItem;
    }

    private static boolean isTamedByStopper(Entity entity) {
        if (!(entity instanceof EntityTameable)) {
            return false;
        }

        EntityTameable tameable = (EntityTameable) entity;
        return tameable.isTamed() && isEntityStopper(tameable.world, tameable.getOwner()) && tameable.getAttackingEntity() != null;
    }

    public static boolean isEntityStopper(Object world, @Nullable Entity entity) {
        return entity != null && WorldInvoker.stopping(world) && entity.equals(WorldInvoker.invoker(world).getStopper());
    }

    public static boolean movableInStoppedTime(Object theWORLD, Entity entity) {
        if (!WorldInvoker.stopping(theWORLD)) {
            return true;
        }

        return entity instanceof EntityLiving && ((EntityLiving) entity).getLeashed();
    }

    public static int getAdjustedCoolDown(int curTimeOverTicks) {
        int max = CommonConfig.timeLimitTicks;
        return Math.max((int) (CommonConfig.maxCoolDownTicks * (max - curTimeOverTicks) / (float) max), 0);
    }

    public static boolean shouldBeSent(EntityPlayerMP serverPlayer, Packet<?> packet) {
        return !((EntityPlayerInvoker) serverPlayer).isLoggedIn() || packet instanceof SPacketSpawnPlayer || packet instanceof SPacketChunkData || packet instanceof SPacketTabComplete || packet instanceof SPacketPlayerListItem || packet instanceof FMLProxyPacket || packet instanceof SPacketCustomPayload || packet instanceof SPacketChat || packet instanceof SPacketKeepAlive || packet instanceof SPacketJoinGame || packet instanceof SPacketDisconnect;
    }

    @SideOnly(Side.CLIENT)
    public static boolean doNotMove(Object mc) {
        Minecraft client = (Minecraft) mc;
        MinecraftInvoker invoker = (MinecraftInvoker) mc;
        WorldInvoker world = WorldInvoker.invoker(client.world);
        return world.timeStopping() && (invoker.isInNPInverse() || !isEntityStopper(client.world, client.player));
    }
}
