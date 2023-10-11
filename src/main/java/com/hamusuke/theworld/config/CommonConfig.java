package com.hamusuke.theworld.config;

import com.hamusuke.theworld.THE_WORLD;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Config(modid = THE_WORLD.MOD_ID, category = "common")
public class CommonConfig {
    public static boolean allowFlyWhenTimeStopping = true;
    public static int timeLimitTicks = 5 * 20;
    public static int maxCoolDownTicks = 5 * 20;

    public static void sync(Configuration config) {
        Property allowFly = config.get("common", "allowFlyWhenTimeStopping", true);
        allowFlyWhenTimeStopping = allowFly.getBoolean(true);

        Property lim = config.get("common", "timeLimitTicks", 5 * 20);
        timeLimitTicks = lim.getInt(5 * 20);

        Property cd = config.get("common", "maxCoolDownTicks", 5 * 20);
        maxCoolDownTicks = cd.getInt(5 * 20);
    }
}
