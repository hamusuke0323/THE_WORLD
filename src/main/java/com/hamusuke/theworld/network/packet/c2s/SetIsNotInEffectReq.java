package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.PlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class SetIsNotInEffectReq implements Packet {
    @Override
    public boolean handle(Context context) {
        var player = context.getSender();
        if (player != null) {
            ((PlayerInvoker) player).setIsInEffect(false);
        }

        return true;
    }
}
