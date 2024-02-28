package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

import java.util.Objects;

public class SetIsInEffectFlagNotify implements Packet {
    private final int playerId;
    private final boolean flag;

    public SetIsInEffectFlagNotify(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
        this.flag = buf.readBoolean();
    }

    public SetIsInEffectFlagNotify(ServerPlayer serverPlayer) {
        this.playerId = serverPlayer.getId();
        this.flag = PlayerInvoker.invoker(serverPlayer).isInEffect();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.playerId);
        buf.writeBoolean(this.flag);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            var entity = Objects.requireNonNull(mc.level).getEntity(this.playerId);
            if (entity != null && TheWorldUtil.isEntityStopper(entity.level(), entity)) {
                PlayerInvoker.invoker(entity).setIsInEffect(this.flag);
            }
        }));

        return true;
    }
}
