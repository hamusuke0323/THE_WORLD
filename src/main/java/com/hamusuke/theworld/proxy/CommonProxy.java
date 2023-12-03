package com.hamusuke.theworld.proxy;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimeS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessS2CPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverS2CPacket;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void preInit(final FMLPreInitializationEvent event) {
    }

    public void onConfigChanged(Configuration config) {
        CommonConfig.sync(config);
    }

    public void onMessage(PlayerSetIsInEffectS2CPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDStopsTimeS2CPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDSuccessS2CPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDTimeOverS2CPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }
}
