package com.hamusuke.theworld.invoker;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface LevelInvoker {
    void stopTime(Player stopper);

    void startTime(Player releaser);

    boolean timeStopping();

    Player getStopper();

    void setTimeLimitTicks(int ticks);

    static LevelInvoker invoker(Object object) {
        return (LevelInvoker) object;
    }

    static boolean stopping(Object world) {
        return stopping((Level) world);
    }

    static boolean stopping(Level world) {
        return world != null && ((LevelInvoker) world).timeStopping();
    }
}
