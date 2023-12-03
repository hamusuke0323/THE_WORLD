package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TimeIsAboutToStopC2SPacket implements Packet<TimeIsAboutToStopC2SPacket> {
    @Override
    public void processPacket(TimeIsAboutToStopC2SPacket message, MessageContext ctx) {
        ((EntityPlayerInvoker) ctx.getServerHandler().player).setIsInEffect(true);
    }
}
