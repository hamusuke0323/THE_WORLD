package com.hamusuke.theworld;

import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.proxy.CommonProxy;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.hamusuke.theworld.THE_WORLD.*;

@Mod(modid = MOD_ID, name = NAME, version = VERSION, guiFactory = "com.hamusuke." + MOD_ID + ".client.gui.screen.ConfigScreenFactory")
public final class THE_WORLD {
	public static final String MOD_ID = "theworld";
	public static final String NAME = "THE WORLD";
	public static final String VERSION = "3.5.0";
	@SidedProxy(modId = MOD_ID, serverSide = "com.hamusuke.theworld.proxy.CommonProxy", clientSide = "com.hamusuke.theworld.proxy.ClientProxy")
	public static CommonProxy PROXY;
	public static final ResourceLocation THE_WORLD_ID = new ResourceLocation(MOD_ID, "the_world");
	public static final SoundEvent THE_WORLD_SOUND = new SoundEvent(THE_WORLD_ID).setRegistryName(THE_WORLD_ID);
	public static final ResourceLocation THE_WORLD_RELEASED_ID = new ResourceLocation(MOD_ID, "the_world_released");
	public static final SoundEvent THE_WORLD_RELEASED = new SoundEvent(THE_WORLD_RELEASED_ID).setRegistryName(THE_WORLD_RELEASED_ID);
	private static Configuration config;

	public static Configuration getConfig() {
		return config;
	}

	private static void syncConfig(boolean load) {
		if (load) {
			config.load();
		}

		PROXY.onConfigChanged(config);
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		NetworkManager.registerPackets();
		PROXY.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
		config = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig(true);
	}

	@SubscribeEvent
	public void onBreakSpeed(final PlayerEvent.BreakSpeed event) {
		if (WorldInvoker.stopping(event.getEntityPlayer().world)) {
			if (!event.getEntityPlayer().onGround) {
				event.setNewSpeed(event.getNewSpeed() * 5.0F);
			}
			if (event.getEntityPlayer().isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(event.getEntityPlayer())) {
				event.setNewSpeed(event.getNewSpeed() * 5.0F);
			}
			event.setNewSpeed(event.getNewSpeed() * 5.0F);
		}
	}

	@SubscribeEvent
	public void onSoundEventRegister(final RegistryEvent.Register<SoundEvent> soundEventRegister) {
		soundEventRegister.getRegistry().registerAll(THE_WORLD_SOUND, THE_WORLD_RELEASED);
	}

	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (MOD_ID.equals(event.getModID())) {
			syncConfig(false);
		}
	}
}
