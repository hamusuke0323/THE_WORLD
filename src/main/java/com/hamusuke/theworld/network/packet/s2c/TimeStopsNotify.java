package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.TheWorld;
import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

import java.util.Objects;

public class TimeStopsNotify implements Packet {
    private final int playerId;

    public TimeStopsNotify(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
    }

    public TimeStopsNotify(Player player) {
        this.playerId = player.getId();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerId);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            var entity = Objects.requireNonNull(mc.level).getEntity(this.playerId);
            if (entity instanceof Player player) {
                mc.tell(() -> mc.getSoundManager().pause());
                TheWorldUtil.playTheWorldSE(player, TheWorld.THE_WORLD_SE_ID);
                LevelInvoker.invoker(mc.level).stopTime(player);

                if (!TheWorldUtil.amIStopper(player)) {
                    mc.tell(() -> MinecraftInvoker.invoker(mc).loadGrayscaleShader());
                }
            }
        }));

        return true;
    }
}
