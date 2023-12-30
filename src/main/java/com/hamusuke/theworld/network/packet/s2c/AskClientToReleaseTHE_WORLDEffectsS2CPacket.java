package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AskClientToReleaseTHE_WORLDEffectsS2CPacket implements Packet<AskClientToReleaseTHE_WORLDEffectsS2CPacket> {
    @Override
    public void processPacket(AskClientToReleaseTHE_WORLDEffectsS2CPacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }
}
