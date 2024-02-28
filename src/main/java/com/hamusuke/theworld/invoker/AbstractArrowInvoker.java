package com.hamusuke.theworld.invoker;

public interface AbstractArrowInvoker {
    boolean wasTimeStopping();

    static AbstractArrowInvoker invoker(Object arrow) {
        return (AbstractArrowInvoker) arrow;
    }
}
