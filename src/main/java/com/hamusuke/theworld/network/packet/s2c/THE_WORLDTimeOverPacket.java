package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class THE_WORLDTimeOverPacket implements Packet<THE_WORLDTimeOverPacket> {
    @Override
    public void processPacket(THE_WORLDTimeOverPacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }
}
