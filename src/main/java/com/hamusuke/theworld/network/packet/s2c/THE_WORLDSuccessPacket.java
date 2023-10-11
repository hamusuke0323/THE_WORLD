package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class THE_WORLDSuccessPacket implements Packet<THE_WORLDSuccessPacket> {
    @Override
    public void processPacket(THE_WORLDSuccessPacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }
}
