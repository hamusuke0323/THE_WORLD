package com.hamusuke.theworld.invoker;

import net.minecraft.world.phys.Vec3;

public interface PlayerInvoker {
    boolean isLoggedIn();

    void setLoggedIn(boolean isLoggedIn);

    void setIsInEffect(boolean flag);

    boolean isInEffect();

    int getCoolDownTicks();

    void setCoolDownTicks(int coolDownTicks);

    Vec3 getLastTheWorldPos();

    void setLastTheWorldPos(Vec3 pos);

    int getLastTheWorldPosExpiresTicks();

    void setLastTheWorldPosExpiresTicks(int tick);

    default boolean shouldUseLastTheWorldPos() {
        return this.getLastTheWorldPosExpiresTicks() > 0;
    }

    default boolean canDeclareTheWorld() {
        return this.getCoolDownTicks() <= 0;
    }

    static PlayerInvoker invoker(Object player) {
        return (PlayerInvoker) player;
    }
}
