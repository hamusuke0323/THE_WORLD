package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DeclareTHE_WORLDPacket implements Packet<DeclareTHE_WORLDPacket> {
    @Override
    public void processPacket(DeclareTHE_WORLDPacket message, MessageContext ctx) {
        World world = ctx.getServerHandler().player.world;
        if (world != null) {
            ((WorldInvoker) world).stopTime(ctx.getServerHandler().player);
        }
    }
}
