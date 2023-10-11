package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class THE_WORLDStopsTimePacket implements Packet<THE_WORLDStopsTimePacket> {
    private int playerId;

    public THE_WORLDStopsTimePacket() {
    }

    public THE_WORLDStopsTimePacket(EntityPlayer player) {
        this.playerId = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerId);
    }

    @Override
    public void processPacket(THE_WORLDStopsTimePacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }

    public int getPlayerId() {
        return this.playerId;
    }
}
