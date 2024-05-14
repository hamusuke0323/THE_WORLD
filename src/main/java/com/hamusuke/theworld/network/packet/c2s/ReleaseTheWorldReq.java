package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class ReleaseTheWorldReq implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                player.getServer().executeIfPossible(() -> LevelInvoker.invoker(player.serverLevel()).startTime(player));
            }
        });

        return true;
    }
}
