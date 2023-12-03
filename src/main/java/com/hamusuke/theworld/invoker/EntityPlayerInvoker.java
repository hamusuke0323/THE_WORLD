package com.hamusuke.theworld.invoker;

public interface EntityPlayerInvoker {
    boolean isLoggedIn();

    void setLoggedIn(boolean isLoggedIn);

    void setIsInEffect(boolean flag);

    boolean isInEffect();

    int getCoolDownTicks();

    void setCoolDownTicks(int coolDownTicks);

    default boolean canTHE_WORLD() {
        return this.getCoolDownTicks() <= 0;
    }
}
