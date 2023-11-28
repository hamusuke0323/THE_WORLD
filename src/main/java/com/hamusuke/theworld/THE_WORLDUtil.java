package com.hamusuke.theworld;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class THE_WORLDUtil {
    public static final int THE_WORLD_EFFECT_TICK = 40;

    public static boolean updatableInStoppedTime(Object theWORLD, Entity entity) {
        if (!WorldInvoker.stopping(theWORLD)) {
            return true;
        }

        return isEntityStopper(entity) || isTamedByStopper((WorldInvoker) theWORLD, entity) || entity.getRecursivePassengersByType(EntityPlayer.class).size() == 1 || entity instanceof EntityItem;
    }

    private static boolean isTamedByStopper(WorldInvoker world, Entity entity) {
        if (!(entity instanceof EntityTameable)) {
            return false;
        }

        EntityTameable tameable = (EntityTameable) entity;
        return tameable.isTamed() && tameable.getOwner() != null && tameable.getOwner().equals(world.getStopper()) && tameable.getAttackingEntity() != null;
    }

    public static boolean isEntityStopper(Entity entity) {
        return WorldInvoker.stopping(entity.world) && entity.equals(WorldInvoker.invoker(entity.world).getStopper());
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

    public static boolean shouldBeSent(Packet<?> packet) {
        return packet instanceof FMLProxyPacket || packet instanceof SPacketCustomPayload || packet instanceof SPacketChat || packet instanceof SPacketKeepAlive || packet instanceof SPacketJoinGame || packet instanceof SPacketDisconnect;
    }
}
