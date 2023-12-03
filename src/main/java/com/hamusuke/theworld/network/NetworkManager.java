package com.hamusuke.theworld.network;

import com.hamusuke.theworld.network.packet.c2s.*;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimeS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverS2CPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class NetworkManager {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("THE_WORLD.net");
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Supplier<Integer> generator = ID::getAndIncrement;

    public static void registerPackets() {
        registerClient2ServerPackets();
        registerServer2ClientPackets();
    }

    private static void registerClient2ServerPackets() {
        INSTANCE.registerMessage(DeclareTHE_WORLDC2SPacket.class, DeclareTHE_WORLDC2SPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(ReleaseLeashedEntitiesRequestC2SPacket.class, ReleaseLeashedEntitiesRequestC2SPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(ReleaseTHE_WORLDC2SPacket.class, ReleaseTHE_WORLDC2SPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(TimeIsAboutToStopC2SPacket.class, TimeIsAboutToStopC2SPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(TimeStoppedC2SPacket.class, TimeStoppedC2SPacket.class, generator.get(), Side.SERVER);
    }

    private static void registerServer2ClientPackets() {
        INSTANCE.registerMessage(PlayerSetIsInEffectS2CPacket.class, PlayerSetIsInEffectS2CPacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDStopsTimeS2CPacket.class, THE_WORLDStopsTimeS2CPacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDSuccessS2CPacket.class, THE_WORLDSuccessS2CPacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDTimeOverS2CPacket.class, THE_WORLDTimeOverS2CPacket.class, generator.get(), Side.CLIENT);
    }

    public static void sendToClient(IMessage packet, EntityPlayerMP serverPlayer) {
        INSTANCE.sendTo(packet, serverPlayer);
    }

    public static void sendToDimension(IMessage packet, int dim) {
        INSTANCE.sendToDimension(packet, dim);
    }

    public static void sendToServer(IMessage packet) {
        INSTANCE.sendToServer(packet);
    }
}
