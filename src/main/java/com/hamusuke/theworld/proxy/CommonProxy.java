package com.hamusuke.theworld.proxy;

import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimePacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverPacket;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
    public void preInit(final FMLPreInitializationEvent event) {
    }

    public void onConfigChanged(Configuration config) {
        CommonConfig.sync(config);
    }

    public void onMessage(PlayerSetIsInEffectPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDStopsTimePacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDSuccessPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }

    public void onMessage(THE_WORLDTimeOverPacket packet, MessageContext ctx) {
        throw new IllegalStateException("DO NOT handle this packet on server-side!");
    }
}
