package com.hamusuke.theworld.network;

import com.hamusuke.theworld.network.packet.c2s.DeclareTHE_WORLDPacket;
import com.hamusuke.theworld.network.packet.c2s.ReleaseTHE_WORLDPacket;
import com.hamusuke.theworld.network.packet.c2s.TimeIsAboutToStopPacket;
import com.hamusuke.theworld.network.packet.c2s.TimeStoppedPacket;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimePacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
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

    public static void init() {
        INSTANCE.registerMessage(DeclareTHE_WORLDPacket.class, DeclareTHE_WORLDPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(ReleaseTHE_WORLDPacket.class, ReleaseTHE_WORLDPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(TimeIsAboutToStopPacket.class, TimeIsAboutToStopPacket.class, generator.get(), Side.SERVER);
        INSTANCE.registerMessage(TimeStoppedPacket.class, TimeStoppedPacket.class, generator.get(), Side.SERVER);

        INSTANCE.registerMessage(PlayerSetIsInEffectPacket.class, PlayerSetIsInEffectPacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDStopsTimePacket.class, THE_WORLDStopsTimePacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDSuccessPacket.class, THE_WORLDSuccessPacket.class, generator.get(), Side.CLIENT);
        INSTANCE.registerMessage(THE_WORLDTimeOverPacket.class, THE_WORLDTimeOverPacket.class, generator.get(), Side.CLIENT);
    }

    public static void sendToClient(IMessage packet, EntityPlayerMP serverPlayer) {
        INSTANCE.sendTo(packet, serverPlayer);
    }

    public static void sendToDimension(IMessage packet, MinecraftServer server, int dim) {
        server.getPlayerList().getPlayers().stream().filter(e -> e.dimension == dim).forEach(e -> sendToClient(packet, e));
    }

    public static void sendToServer(IMessage packet) {
        INSTANCE.sendToServer(packet);
    }
}
