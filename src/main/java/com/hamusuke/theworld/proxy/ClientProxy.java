package com.hamusuke.theworld.proxy;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.client.THE_WORLDClient;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.s2c.PlayerSetIsInEffectPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimePacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Supplier<MinecraftInvoker> mcInvoker = () -> (MinecraftInvoker) mc;
    private static final Supplier<WorldInvoker> invoker = () -> (WorldInvoker) mc.world;

    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(THE_WORLDClient.getInstance());
    }

    @Override
    public synchronized void onMessage(PlayerSetIsInEffectPacket packet, MessageContext ctx) {
        Entity entity = mc.world.getEntityByID(packet.getPlayerId());
        if (invoker.get().getStopper().equals(entity)) {
            ((EntityPlayerInvoker) invoker.get().getStopper()).setIsInEffect(packet.getFlag());
        }
    }

    @Override
    public synchronized void onMessage(THE_WORLDStopsTimePacket packet, MessageContext ctx) {
        Entity entity = mc.world.getEntityByID(packet.getPlayerId());
        if (entity instanceof EntityPlayer) {
            mc.getSoundHandler().pauseSounds();
            this.playTHE_WORLD_SE(mc.player, (EntityPlayer) entity, THE_WORLD.THE_WORLD_ID);
            invoker.get().stopTime((EntityPlayer) entity);
        }
    }

    private void playTHE_WORLD_SE(EntityPlayer client, EntityPlayer stopper, ResourceLocation resourceLocation) {
        if (client.equals(stopper)) {
            mc.getSoundHandler().playSound(new PositionedSoundRecord(resourceLocation, SoundCategory.PLAYERS, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
        }
    }

    @Override
    public synchronized void onMessage(THE_WORLDSuccessPacket packet, MessageContext ctx) {
        mcInvoker.get().onDeclaredTheWorld();
    }

    @Override
    public synchronized void onMessage(THE_WORLDTimeOverPacket packet, MessageContext ctx) {
        EntityPlayer stopper = invoker.get().getStopper();
        invoker.get().startTime(stopper);
        boolean isStopper = mc.player.equals(stopper);
        mc.getSoundHandler().resumeSounds();
        if (isStopper) {
            mc.getSoundHandler().stop(THE_WORLD.THE_WORLD_ID.toString(), SoundCategory.PLAYERS);
            mc.getSoundHandler().stop(THE_WORLD.THE_WORLD_RELEASED_ID.toString(), SoundCategory.PLAYERS);
            mcInvoker.get().finishNPInverse();
            this.playTHE_WORLD_SE(mc.player, stopper, THE_WORLD.THE_WORLD_RELEASED_ID);
        }
    }
}
