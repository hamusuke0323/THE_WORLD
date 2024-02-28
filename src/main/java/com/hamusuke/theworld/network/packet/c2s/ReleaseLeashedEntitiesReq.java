package com.hamusuke.theworld.network.packet.c2s;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class ReleaseLeashedEntitiesReq implements Packet {
    @Override
    public boolean handle(Context context) {
        var player = context.getSender();
        if (player != null && !player.isSpectator() && LevelInvoker.stopping(player.serverLevel())) {
            player.serverLevel().getEntities(EntityTypeTest.forClass(Mob.class), mob -> player.equals(mob.getLeashHolder())).forEach(mob -> mob.dropLeash(true, !player.isCreative()));
        }

        return true;
    }
}
