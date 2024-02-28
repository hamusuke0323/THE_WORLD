package com.hamusuke.theworld.network;

import com.hamusuke.theworld.TheWorld;
import com.hamusuke.theworld.network.packet.Packet;
import com.hamusuke.theworld.network.packet.c2s.*;
import com.hamusuke.theworld.network.packet.s2c.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.SimpleChannel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class NetworkManager {
    private static final int PROTOCOL_VERSION = 1;
    private static final SimpleChannel MAIN = ChannelBuilder
            .named(new ResourceLocation(TheWorld.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Supplier<Integer> generator = ID::incrementAndGet;

    public static void registerPackets() {
        registerC2SPackets();
        registerS2CPackets();
    }

    private static void registerC2SPackets() {
        MAIN.messageBuilder(ReleaseLeashedEntitiesReq.class, generator.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new ReleaseLeashedEntitiesReq()).consumerNetworkThread(ReleaseLeashedEntitiesReq::handle).add();
        MAIN.messageBuilder(ReleaseTheWorldReq.class, generator.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new ReleaseTheWorldReq()).consumerNetworkThread(ReleaseTheWorldReq::handle).add();
        MAIN.messageBuilder(SetIsInEffectReq.class, generator.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new SetIsInEffectReq()).consumerNetworkThread(SetIsInEffectReq::handle).add();
        MAIN.messageBuilder(SetIsNotInEffectReq.class, generator.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new SetIsNotInEffectReq()).consumerNetworkThread(SetIsNotInEffectReq::handle).add();
        MAIN.messageBuilder(TheWorldReq.class, generator.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new TheWorldReq()).consumerNetworkThread(TheWorldReq::handle).add();
    }

    private static void registerS2CPackets() {
        MAIN.messageBuilder(ReleaseTheWorldEffectsNotify.class, generator.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(buf -> new ReleaseTheWorldEffectsNotify()).consumerNetworkThread(ReleaseTheWorldEffectsNotify::handle).add();
        MAIN.messageBuilder(SetIsInEffectFlagNotify.class, generator.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(SetIsInEffectFlagNotify::write).decoder(SetIsInEffectFlagNotify::new).consumerNetworkThread(SetIsInEffectFlagNotify::handle).add();
        MAIN.messageBuilder(TheWorldSuccNotify.class, generator.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(buf -> new TheWorldSuccNotify()).consumerNetworkThread(TheWorldSuccNotify::handle).add();
        MAIN.messageBuilder(TimeOverNotify.class, generator.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(buf -> new TimeOverNotify()).consumerNetworkThread(TimeOverNotify::handle).add();
        MAIN.messageBuilder(TimeStopsNotify.class, generator.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(TimeStopsNotify::write).decoder(TimeStopsNotify::new).consumerNetworkThread(TimeStopsNotify::handle).add();
    }

    public static void sendToClient(Packet obj, ServerPlayer serverPlayer) {
        MAIN.send(obj, serverPlayer.connection.getConnection());
    }

    public static void sendToDimension(Packet obj, ServerPlayer serverPlayer) {
        MAIN.send(obj, new PacketTarget(packet -> serverPlayer.serverLevel().players().forEach(p -> p.connection.send(packet)), NetworkDirection.PLAY_TO_CLIENT));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(Object obj) {
        if (Minecraft.getInstance().player != null) {
            MAIN.send(obj, Minecraft.getInstance().player.connection.getConnection());
        }
    }
}
