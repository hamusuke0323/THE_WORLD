package com.hamusuke.theworld.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface Packet<T extends IMessage> extends IMessage, IMessageHandler<T, IMessage> {
    void processPacket(T message, MessageContext ctx);

    @Override
    default void fromBytes(ByteBuf buf) {
    }

    @Override
    default void toBytes(ByteBuf buf) {
    }

    @Override
    default IMessage onMessage(T message, MessageContext ctx) {
        this.processPacket(message, ctx);
        return null;
    }
}
