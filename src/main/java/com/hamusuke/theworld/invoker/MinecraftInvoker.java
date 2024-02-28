package com.hamusuke.theworld.invoker;

public interface MinecraftInvoker {
    void theWorld();

    boolean isInNPInverse();

    void finishNPInverse();

    int getInverseTick();

    void loadGrayscaleShader();

    void unloadGrayscaleShader();

    static MinecraftInvoker invoker(Object mc) {
        return (MinecraftInvoker) mc;
    }
}
