package com.hamusuke.theworld;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TheWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheWorldConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final BooleanValue ALLOW_FLY_WHEN_TIME_STOPPING = BUILDER
            .comment("Whether to allow fly when time is stopping")
            .define("allowFlyWhenTimeStopping", true);

    public static final BooleanValue AUTO_THE_WORLD = BUILDER
            .comment("Whether to declare the world automatically")
            .define("autoTheWorld", false);

    public static final IntValue TIME_LIMIT_TICKS = BUILDER
            .comment("A time limit ticks")
            .defineInRange("timeLimitTicks", 5 * 20, 1, Integer.MAX_VALUE);

    public static final IntValue MAX_COOL_DOWN_TICKS = BUILDER
            .comment("A max cool down ticks")
            .defineInRange("maxCoolDownTicks", 5 * 20, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean allowFlyWhenTimeStopping;
    public static boolean autoTheWorld;
    public static int timeLimitTicks;
    public static int maxCoolDownTicks;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        allowFlyWhenTimeStopping = ALLOW_FLY_WHEN_TIME_STOPPING.get();
        autoTheWorld = AUTO_THE_WORLD.get();
        timeLimitTicks = TIME_LIMIT_TICKS.get();
        maxCoolDownTicks = MAX_COOL_DOWN_TICKS.get();
    }
}
