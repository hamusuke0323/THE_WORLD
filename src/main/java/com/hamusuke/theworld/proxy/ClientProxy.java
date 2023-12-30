package com.hamusuke.theworld.proxy;

import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.client.THE_WORLDClient;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.s2c.*;
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

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Supplier<MinecraftInvoker> mcInvoker = () -> (MinecraftInvoker) mc;
    private static final Supplier<WorldInvoker> invoker = () -> (WorldInvoker) mc.world;
    private int playerIdCache = -1;

    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(THE_WORLDClient.getInstance());
    }

    @Override
    public void tick() {
        if (this.playerIdCache >= 0) {
            Entity entity = mc.world.getEntityByID(this.playerIdCache);
            if (entity != null) {
                this.playerIdCache = -1;
                this.stop(entity);
            }
        }
    }

    private static boolean amIStopper(Entity stopper) {
        return mc.player.equals(stopper);
    }

    @Override
    public void onMessage(AskClientToReleaseTHE_WORLDEffectsS2CPacket packet, MessageContext ctx) {
        mc.addScheduledTask(mcInvoker.get()::unloadGrayscaleShader);
        mc.addScheduledTask(mc.getSoundHandler()::resumeSounds);
        mcInvoker.get().finishNPInverse();
    }

    @Override
    public synchronized void onMessage(THE_WORLDStopsTimeS2CPacket packet, MessageContext ctx) {
        Entity entity = mc.world.getEntityByID(packet.getPlayerId());
        if (entity == null) {
            this.playerIdCache = packet.getPlayerId();
        }
        this.stop(entity);
    }

    @Override
    public synchronized void onMessage(PlayerSetIsInEffectS2CPacket packet, MessageContext ctx) {
        Entity entity = mc.world.getEntityByID(packet.getPlayerId());
        if (entity != null && THE_WORLDUtil.isEntityStopper(entity.world, entity)) {
            ((EntityPlayerInvoker) invoker.get().getStopper()).setIsInEffect(packet.getFlag());
        }
    }

    private void stop(@Nullable Entity entity) {
        if (entity instanceof EntityPlayer) {
            mc.addScheduledTask(mc.getSoundHandler()::pauseSounds);
            this.playTHE_WORLD_SE((EntityPlayer) entity, THE_WORLD.THE_WORLD_ID);
            invoker.get().stopTime((EntityPlayer) entity);

            if (!amIStopper(entity)) {
                mc.addScheduledTask(mcInvoker.get()::loadGrayscaleShader);
            }
        }
    }

    private void playTHE_WORLD_SE(EntityPlayer stopper, ResourceLocation resourceLocation) {
        if (amIStopper(stopper)) {
            mc.addScheduledTask(() -> mc.getSoundHandler().playSound(new PositionedSoundRecord(resourceLocation, SoundCategory.PLAYERS, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F)));
        }
    }

    @Override
    public synchronized void onMessage(THE_WORLDSuccessS2CPacket packet, MessageContext ctx) {
        mcInvoker.get().onDeclaredTheWorld();
    }

    @Override
    public synchronized void onMessage(THE_WORLDTimeOverS2CPacket packet, MessageContext ctx) {
        EntityPlayer stopper = invoker.get().getStopper();
        invoker.get().startTime(stopper);
        mc.addScheduledTask(mcInvoker.get()::unloadGrayscaleShader);
        mc.addScheduledTask(mc.getSoundHandler()::resumeSounds);

        if (amIStopper(stopper)) {
            mcInvoker.get().finishNPInverse();
            this.playTHE_WORLD_SE(stopper, THE_WORLD.THE_WORLD_RELEASED_ID);
        }
    }
}
