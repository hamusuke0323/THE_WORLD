package com.hamusuke.theworld.invoker;

public interface MinecraftInvoker {
    void onDeclaredTheWorld();

    boolean isInNPInverse();

    void finishNPInverse();

    int getInverseTick();

    void loadGrayscaleShader();

    void unloadGrayscaleShader();
}
