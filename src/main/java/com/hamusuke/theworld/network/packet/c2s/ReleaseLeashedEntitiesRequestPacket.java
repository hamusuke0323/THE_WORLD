package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ReleaseLeashedEntitiesRequestPacket implements Packet<ReleaseLeashedEntitiesRequestPacket> {
    @Override
    public void processPacket(ReleaseLeashedEntitiesRequestPacket message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (!player.isSpectator() && WorldInvoker.stopping(player.world)) {
            player.world.getEntities(EntityLiving.class, living -> player.equals(living.getLeashHolder()))
                    .forEach(living -> living.clearLeashed(true, !player.isCreative()));
        }
    }
}
