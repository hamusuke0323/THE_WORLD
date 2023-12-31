package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerSetIsInEffectS2CPacket implements Packet<PlayerSetIsInEffectS2CPacket> {
    private int playerId;
    private boolean flag;

    public PlayerSetIsInEffectS2CPacket() {
    }

    public PlayerSetIsInEffectS2CPacket(EntityPlayer serverPlayer) {
        this.playerId = serverPlayer.getEntityId();
        this.flag = ((EntityPlayerInvoker) serverPlayer).isInEffect();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = buf.readInt();
        this.flag = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeBoolean(this.flag);
    }

    @Override
    public void processPacket(PlayerSetIsInEffectS2CPacket message, MessageContext ctx) {
        THE_WORLD.PROXY.onMessage(message, ctx);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public boolean getFlag() {
        return this.flag;
    }
}
