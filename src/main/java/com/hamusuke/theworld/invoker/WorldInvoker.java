package com.hamusuke.theworld.invoker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface WorldInvoker {
    void stopTime(EntityPlayer stopper);

    void startTime(EntityPlayer releaser);

    boolean timeStopping();

    EntityPlayer getStopper();

    void setTimeLimitTicks(int ticks);

    static boolean stopping(World world) {
        return world != null && ((WorldInvoker) world).timeStopping();
    }
}
