package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReleaseTHE_WORLDPacket implements Packet<ReleaseTHE_WORLDPacket> {
    @Override
    public void processPacket(ReleaseTHE_WORLDPacket message, MessageContext ctx) {
        ((WorldInvoker) ctx.getServerHandler().player.world).startTime(ctx.getServerHandler().player);
    }
}
