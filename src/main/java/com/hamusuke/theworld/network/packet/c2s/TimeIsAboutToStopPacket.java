package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TimeIsAboutToStopPacket implements Packet<TimeIsAboutToStopPacket> {
    @Override
    public void processPacket(TimeIsAboutToStopPacket message, MessageContext ctx) {
        ((EntityPlayerInvoker) ctx.getServerHandler().player).setIsInEffect(true);
    }
}
