package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class THE_WORLDSuccessS2CPacket implements Packet<THE_WORLDSuccessS2CPacket> {
    @Override
    public void processPacket(THE_WORLDSuccessS2CPacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }
}
