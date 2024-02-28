package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class TheWorldReq implements Packet {
    @Override
    public boolean handle(Context context) {
        var player = context.getSender();
        if (player == null) {
            return true;
        }

        var world = player.serverLevel();
        LevelInvoker.invoker(world).stopTime(context.getSender());

        return true;
    }
}
