package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public class TheWorldSuccNotify implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftInvoker.invoker(Minecraft.getInstance()).theWorld()));
        return true;
    }
}
