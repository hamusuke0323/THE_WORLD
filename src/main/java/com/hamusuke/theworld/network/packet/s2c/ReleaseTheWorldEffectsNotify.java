package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public class ReleaseTheWorldEffectsNotify implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            var invoker = MinecraftInvoker.invoker(mc);
            mc.tell(() -> {
                invoker.unloadGrayscaleShader();
                mc.getSoundManager().resume();
            });
            invoker.finishNPInverse();
        }));

        return true;
    }
}
