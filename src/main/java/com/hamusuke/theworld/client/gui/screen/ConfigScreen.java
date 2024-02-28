package com.hamusuke.theworld.client.gui.screen;

import com.hamusuke.theworld.TheWorld;
import com.hamusuke.theworld.TheWorldConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigScreen extends Screen {
    private static final Component ALLOW_FLY = Component.translatable(TheWorld.MOD_ID + ".config.allowFly");
    private static final Component AUTO_THE_WORLD = Component.translatable(TheWorld.MOD_ID + ".config.autoTheWorld");
    private static final Component TIME_LIMIT = Component.translatable(TheWorld.MOD_ID + ".config.timeLimit");
    private static final Component MAX_COOL_DOWN = Component.translatable(TheWorld.MOD_ID + ".config.maxCoolDown");
    private final Screen parent;
    private EditBox timeLimit;
    private EditBox maxCoolDown;

    public ConfigScreen(Screen parent) {
        super(Component.translatable(TheWorld.MOD_ID + ".config.title"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(CycleButton.onOffBuilder(TheWorldConfig.allowFlyWhenTimeStopping).create(this.width / 4, this.height / 2 - 60, this.width / 2, 20, ALLOW_FLY, (cycleButton, aBoolean) -> {
            TheWorldConfig.allowFlyWhenTimeStopping = aBoolean;
        }));

        this.addRenderableWidget(CycleButton.onOffBuilder(TheWorldConfig.autoTheWorld).create(this.width / 4, this.height / 2 - 40, this.width / 2, 20, AUTO_THE_WORLD, (cycleButton, aBoolean) -> {
            TheWorldConfig.autoTheWorld = aBoolean;
        }));

        var w = this.addRenderableWidget(new FocusableTextWidget(this.width / 4, TIME_LIMIT, this.font, false));
        w.setX(this.width / 2 - this.font.width(TIME_LIMIT) / 2);
        w.setY(this.height / 2 - 10);

        this.timeLimit = new EditBox(this.font, this.width / 4, this.height / 2, this.width / 2, 20, this.timeLimit, TIME_LIMIT);
        this.timeLimit.setValue(TheWorldConfig.timeLimitTicks + "");
        this.timeLimit.setMaxLength(10);
        this.timeLimit.setResponder(s -> TheWorldConfig.timeLimitTicks = this.onEdit(s, this.timeLimit, TheWorldConfig.TIME_LIMIT_TICKS));
        this.addWidget(this.timeLimit);

        w = this.addRenderableWidget(new FocusableTextWidget(this.width / 4, MAX_COOL_DOWN, this.font, false));
        w.setX(this.width / 2 - this.font.width(MAX_COOL_DOWN) / 2);
        w.setY(this.height / 2 + 30);

        this.maxCoolDown = new EditBox(this.font, this.width / 4, this.height / 2 + 40, this.width / 2, 20, this.maxCoolDown, MAX_COOL_DOWN);
        this.maxCoolDown.setValue(TheWorldConfig.maxCoolDownTicks + "");
        this.maxCoolDown.setMaxLength(10);
        this.maxCoolDown.setResponder(s -> TheWorldConfig.maxCoolDownTicks = this.onEdit(s, this.maxCoolDown, TheWorldConfig.MAX_COOL_DOWN_TICKS));
        this.addWidget(this.maxCoolDown);

        this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 4, this.height - 20, this.width / 2, 20).build());
    }

    private int onEdit(String s, EditBox editBox, IntValue configIn) {
        try {
            int i = Integer.parseInt(s);
            editBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
            return i;
        } catch (NumberFormatException e) {
            editBox.setTextColor(Mth.color(1.0F, 0.0F, 0.0F));
            return configIn.get();
        }
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        p_281549_.drawCenteredString(this.font, this.getTitle(), this.width / 2, 10, 16777215);

        this.timeLimit.render(p_281549_, p_281550_, p_282878_, p_282465_);
        this.maxCoolDown.render(p_281549_, p_281550_, p_282878_, p_282465_);
    }

    @Override
    public void removed() {
        TheWorldConfig.ALLOW_FLY_WHEN_TIME_STOPPING.set(TheWorldConfig.allowFlyWhenTimeStopping);
        TheWorldConfig.AUTO_THE_WORLD.set(TheWorldConfig.autoTheWorld);
        TheWorldConfig.TIME_LIMIT_TICKS.set(TheWorldConfig.timeLimitTicks);
        TheWorldConfig.MAX_COOL_DOWN_TICKS.set(TheWorldConfig.maxCoolDownTicks);
        TheWorldConfig.SPEC.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
