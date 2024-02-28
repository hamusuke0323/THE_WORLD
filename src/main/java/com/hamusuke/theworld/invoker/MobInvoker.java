package com.hamusuke.theworld.invoker;

public interface MobInvoker {
    void tickLeashV();

    void updateControlFlagsV();

    static MobInvoker invoker(Object mob) {
        return (MobInvoker) mob;
    }
}
