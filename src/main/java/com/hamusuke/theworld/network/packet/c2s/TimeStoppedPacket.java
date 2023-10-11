package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TimeStoppedPacket implements Packet<TimeStoppedPacket> {
    @Override
    public void processPacket(TimeStoppedPacket message, MessageContext ctx) {
        ((EntityPlayerInvoker) ctx.getServerHandler().player).setIsInEffect(false);
    }
}
