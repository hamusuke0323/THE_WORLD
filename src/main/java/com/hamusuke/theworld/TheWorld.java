package com.hamusuke.theworld;

import com.hamusuke.theworld.client.TheWorldClient;
import com.hamusuke.theworld.client.gui.screen.ConfigScreen;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.ReleaseTheWorldEffectsNotify;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

import static com.hamusuke.theworld.client.TheWorldClient.RELEASE_LEASHED_KEY;
import static com.hamusuke.theworld.client.TheWorldClient.THE_WORLD_KEY;

@Mod(TheWorld.MOD_ID)
public class TheWorld {
    public static final String MOD_ID = "theworld";
    public static final ResourceLocation THE_WORLD_SE_ID = new ResourceLocation(MOD_ID, "the_world");
    public static final ResourceLocation THE_WORLD_RELEASED_ID = new ResourceLocation(MOD_ID, "the_world_released");
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    private static final RegistryObject<SoundEvent> THE_WORLD_SE = SOUND_EVENTS.register("the_world", () -> SoundEvent.createVariableRangeEvent(THE_WORLD_SE_ID));
    private static final RegistryObject<SoundEvent> THE_WORLD_RELEASED_SE = SOUND_EVENTS.register("the_world_released", () -> SoundEvent.createVariableRangeEvent(THE_WORLD_RELEASED_ID));

    public TheWorld() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        SOUND_EVENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TheWorldConfig.SPEC);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerKeyMapping);
    }

    private static void releaseTheWorldEffects(ResourceKey<Level> dim, ServerPlayer player) {
        var worldServer = LevelInvoker.invoker(Objects.requireNonNull(player.getServer(), "this is server-sided event! should never happen.").getLevel(dim));
        if (worldServer != null && worldServer.timeStopping() && Objects.equals(worldServer.getStopper(), player)) {
            worldServer.startTime(player);
            NetworkManager.sendToClient(new ReleaseTheWorldEffectsNotify(), player);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkManager.registerPackets();
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(TheWorldClient.getInstance());
    }

    @OnlyIn(Dist.CLIENT)
    private void registerKeyMapping(final RegisterKeyMappingsEvent event) {
        event.register(THE_WORLD_KEY);
        event.register(RELEASE_LEASHED_KEY);
    }

    @SubscribeEvent
    public void onLivingFall(final LivingFallEvent e) {
        if (LevelInvoker.stopping(e.getEntity().level())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onChangedDim(final PlayerChangedDimensionEvent event) {
        releaseTheWorldEffects(event.getFrom(), (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onCameFromTheEnd(final PlayerRespawnEvent event) {
        if (event.isEndConquered()) {
            releaseTheWorldEffects(Level.END, (ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onDeath(final LivingDeathEvent event) {
        var invoker = LevelInvoker.invoker(event.getEntity().level());
        if (event.getEntity() instanceof ServerPlayer serverPlayer && TheWorldUtil.isEntityStopper(serverPlayer.level(), serverPlayer)) {
            invoker.startTime(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(final BreakSpeed event) {
        if (LevelInvoker.stopping(event.getEntity().level())) {
            if (!event.getEntity().onGround()) {
                event.setNewSpeed(event.getNewSpeed() * 5.0F);
            }
            if (event.getEntity().isUnderWater() && !EnchantmentHelper.hasAquaAffinity(event.getEntity())) {
                event.setNewSpeed(event.getNewSpeed() * 5.0F);
            }

            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }
}
