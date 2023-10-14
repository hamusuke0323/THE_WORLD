package com.hamusuke.theworld.invoker;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;

public interface SoundHandlerInvoker {
    static SoundManager getSoundManager(SoundHandler handler) {
        return ((SoundHandlerInvoker) handler).getSoundManager();
    }

    static SoundManagerInvoker getInvoker(SoundHandler handler) {
        return (SoundManagerInvoker) getSoundManager(handler);
    }

    SoundManager getSoundManager();
}
