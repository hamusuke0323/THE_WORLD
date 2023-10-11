package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.c2s.ReleaseTHE_WORLDPacket;
import com.hamusuke.theworld.network.packet.c2s.TimeIsAboutToStopPacket;
import com.hamusuke.theworld.network.packet.c2s.TimeStoppedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;

import static com.hamusuke.theworld.THE_WORLDUtil.THE_WORLD_EFFECT_TICK;
import static com.hamusuke.theworld.client.THE_WORLDClient.THE_WORLD_KEY;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftInvoker {
    @Shadow
    public EntityPlayerSP player;
    @Shadow
    public WorldClient world;
    @Shadow
    private int rightClickDelayTimer;
    @Shadow
    private boolean isGamePaused;
    @Shadow
    @Final
    public Profiler mcProfiler;
    @Shadow
    public GuiIngame ingameGUI;
    @Shadow
    public EntityRenderer entityRenderer;
    @Shadow
    @Final
    private Tutorial tutorial;
    @Shadow
    public RayTraceResult objectMouseOver;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    @Nullable
    public GuiScreen currentScreen;

    @Shadow
    public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);

    @Shadow
    private int leftClickCounter;

    @Shadow
    protected abstract void runTickMouse() throws IOException;

    @Shadow
    private int joinPlayerCounter;
    @Shadow
    long systemTime;

    @Shadow
    public static long getSystemTime() {
        return 0;
    }

    @Shadow
    @Nullable
    private net.minecraft.network.NetworkManager myNetworkManager;

    @Shadow public abstract void displayInGameMenu();

    @Shadow protected abstract void runTickKeyboard() throws IOException;

    @Unique
    private int NPInverseTick;
    @Unique
    private boolean isInNPInverse;

    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    private void runTick(CallbackInfo ci) throws IOException {
        if (WorldInvoker.stopping(this.world)) {
            if (this.rightClickDelayTimer > 0) {
                --this.rightClickDelayTimer;
            }

            FMLCommonHandler.instance().onPreClientTick();

            if (!this.isGamePaused) {
                if (this.NPInverseTick > 0) {
                    --this.NPInverseTick;
                } else if (this.isInNPInverse) {
                    this.finishNPInverse();
                }
            }

            this.mcProfiler.startSection("gui");

            if (!this.isGamePaused) {
                this.ingameGUI.updateTick();
            }

            this.mcProfiler.endSection();
            this.entityRenderer.getMouseOver(1.0F);
            this.tutorial.onMouseHover(this.world, this.objectMouseOver);
            this.mcProfiler.startSection("gameMode");

            if (!this.isGamePaused && this.world != null) {
                this.playerController.updateController();
            }

            this.mcProfiler.endStartSection("textures");

            if (this.currentScreen == null && this.player != null) {
                if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver)) {
                    this.displayGuiScreen(null);
                } else if (this.player.isPlayerSleeping() && this.world != null) {
                    this.displayGuiScreen(new GuiSleepMP());
                }
            } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping()) {
                this.displayGuiScreen(null);
            }

            if (this.currentScreen != null) {
                this.leftClickCounter = 10000;
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.handleInput();
                } catch (Throwable throwable1) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                    crashreportcategory.addDetail("Screen name", () -> currentScreen.getClass().getCanonicalName());
                    throw new ReportedException(crashreport);
                }

                if (this.currentScreen != null) {
                    try {
                        this.currentScreen.updateScreen();
                    } catch (Throwable throwable) {
                        CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                        CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                        crashreportcategory1.addDetail("Screen name", () -> currentScreen.getClass().getCanonicalName());
                        throw new ReportedException(crashreport1);
                    }
                }
            }

            if (this.currentScreen == null || this.currentScreen.allowUserInput) {
                this.mcProfiler.endStartSection("mouse");
                this.runTickMouseInner();

                if (this.leftClickCounter > 0) {
                    --this.leftClickCounter;
                }

                this.mcProfiler.endStartSection("keyboard");
                this.runTickKeyboardInner();

                while (THE_WORLD_KEY.isPressed()) {
                    if (!this.isInNPInverse()) {
                        NetworkManager.sendToServer(new ReleaseTHE_WORLDPacket());
                    }
                }
            }

            if (this.world != null) {
                if (this.player != null) {
                    ++this.joinPlayerCounter;

                    if (this.joinPlayerCounter == 30) {
                        this.joinPlayerCounter = 0;
                        this.world.joinEntityInSurroundings(this.player);
                    }
                }

                this.mcProfiler.endStartSection("gameRenderer");
                if (!this.isGamePaused) {
                    this.entityRenderer.updateRenderer();
                }

                this.mcProfiler.endStartSection("level");
                if (!this.isGamePaused) {
                    this.world.updateEntities();
                }
            } else if (this.entityRenderer.isShaderActive()) {
                this.entityRenderer.stopUseShader();
            }

            if (this.world != null) {
                if (!this.isGamePaused) {
                    this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                    this.tutorial.update();

                    try {
                        this.world.tick();
                    } catch (Throwable throwable2) {
                        CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                        if (this.world == null) {
                            CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                            crashreportcategory2.addCrashSection("Problem", "Level is null!");
                        } else {
                            this.world.addWorldInfoToCrashReport(crashreport2);
                        }

                        throw new ReportedException(crashreport2);
                    }
                }
            } else if (this.myNetworkManager != null) {
                this.mcProfiler.endStartSection("pendingConnection");
                this.myNetworkManager.processReceivedPackets();
            }

            this.mcProfiler.endSection();
            FMLCommonHandler.instance().onPostClientTick();
            this.systemTime = getSystemTime();

            ci.cancel();
        }
    }

    @Inject(method = "processKeyBinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z", shift = At.Shift.BEFORE), cancellable = true)
    private void processKeyBinds(CallbackInfo ci) {
        if (this.isInNPInverse()) {
            ci.cancel();
        }
    }

    @Unique
    private void runTickKeyboardInner() throws IOException {
        if (this.isInNPInverse()) {
            while (Keyboard.next()) {
                int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                boolean flag = Keyboard.getEventKeyState();

                if (flag) {
                    if (this.currentScreen == null) {
                        if (i == 1) {
                            this.displayInGameMenu();
                        }
                    }

                    KeyBinding.setKeyBindState(i, true);
                    KeyBinding.onTick(i);
                } else {
                    KeyBinding.setKeyBindState(i, false);
                }
            }

            return;
        }

        this.runTickKeyboard();
    }

    @Unique
    private void runTickMouseInner() throws IOException {
        if (this.isInNPInverse()) {
            while (Mouse.next()) {
            }
            return;
        }

        this.runTickMouse();
    }

    @Override
    public void onDeclaredTheWorld() {
        this.isInNPInverse = true;
        this.NPInverseTick = THE_WORLD_EFFECT_TICK;
        NetworkManager.sendToServer(new TimeIsAboutToStopPacket());
    }

    public void finishNPInverse() {
        this.player.timeInPortal = 0.0F;
        this.isInNPInverse = false;
        this.NPInverseTick = 0;
        NetworkManager.sendToServer(new TimeStoppedPacket());
    }

    @Override
    public boolean isInNPInverse() {
        return this.isInNPInverse;
    }

    @Override
    public int getInverseTick() {
        return this.NPInverseTick;
    }
}
