package com.hamusuke.theworld.client.gui.screen;

import com.google.common.collect.Lists;
import com.hamusuke.theworld.THE_WORLD;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ConfigScreen extends GuiConfig {
    public ConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), THE_WORLD.MOD_ID, false, false, I18n.format(THE_WORLD.MOD_ID + ".config.title"));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> commons = Lists.newArrayList();
        commons.add(new DummyConfigElement.DummyCategoryElement("common", THE_WORLD.MOD_ID + ".category.common", Common.class));
        return commons;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        THE_WORLD.getConfig().save();
    }

    public static class Common extends GuiConfigEntries.CategoryEntry {
        public Common(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(this.owningScreen, new ConfigElement(THE_WORLD.getConfig().getCategory("common")).getChildElements(), this.owningScreen.modID, "common", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, GuiConfig.getAbridgedConfigPath(THE_WORLD.getConfig().toString()));
        }
    }
}
