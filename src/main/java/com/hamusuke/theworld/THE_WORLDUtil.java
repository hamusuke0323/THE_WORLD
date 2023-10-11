package com.hamusuke.theworld;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.invoker.WorldInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class THE_WORLDUtil {
    public static final int THE_WORLD_EFFECT_TICK = 40;

    public static boolean updatableInStoppedTime(Object theWORLD, Entity entity) {
        if (!WorldInvoker.stopping((World) theWORLD)) {
            return true;
        }

        return (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed() && ((EntityTameable) entity).getAttackingEntity() != null) || entity instanceof EntityPlayer || entity.getRecursivePassengersByType(EntityPlayer.class).size() == 1 || entity instanceof EntityItem;
    }

    public static boolean movableInStoppedTime(Object theWORLD, Entity entity) {
        return false; // TODO
        /*
        if (!WorldInvoker.stopping((World) theWORLD)) {
            return true;
        }

        return entity instanceof EntityLiving && ((EntityLiving) entity).getLeashed();
        */
    }

    public static int getAdjustedCoolDown(int curTimeOverTicks) {
        int max = CommonConfig.timeLimitTicks;
        return Math.max((int) (CommonConfig.maxCoolDownTicks * (max - curTimeOverTicks) / (float) max), 0);
    }
}
