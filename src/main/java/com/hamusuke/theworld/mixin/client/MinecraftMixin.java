package com.hamusuke.theworld.mixin.client;

import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.c2s.SetIsInEffectReq;
import com.hamusuke.theworld.network.packet.c2s.SetIsNotInEffectReq;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static com.hamusuke.theworld.TheWorldUtil.THE_WORLD_EFFECT_TICK;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftInvoker {
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Final
    public GameRenderer gameRenderer;
    @Shadow
    @Nullable
    public Entity cameraEntity;

    @Shadow
    public abstract boolean isPaused();

    @Unique
    private int NPInverseTick;
    @Unique
    private boolean isInNPInverse;
    @Unique
    private boolean grayscaled;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (LevelInvoker.stopping(this.level) && !this.isPaused()) {
            if (this.NPInverseTick < Mth.clamp(THE_WORLD_EFFECT_TICK - 3, 0, THE_WORLD_EFFECT_TICK)) {
                this.loadGrayscaleShader();
            }

            if (this.NPInverseTick > 0) {
                --this.NPInverseTick;
            } else if (this.isInNPInverse) {
                this.finishNPInverse();
            }
        }
    }

    @Override
    public void theWorld() {
        this.isInNPInverse = true;
        this.NPInverseTick = THE_WORLD_EFFECT_TICK;
        NetworkManager.sendToServer(new SetIsInEffectReq());
    }

    @Override
    public void finishNPInverse() {
        this.player.spinningEffectIntensity = 0.0F;
        this.NPInverseTick = 0;
        this.isInNPInverse = false;
        NetworkManager.sendToServer(new SetIsNotInEffectReq());
    }

    @Override
    public void loadGrayscaleShader() {
        if (this.grayscaled) {
            return;
        }

        this.grayscaled = true;
        this.gameRenderer.checkEntityPostEffect(this.cameraEntity);
    }

    @Override
    public void unloadGrayscaleShader() {
        this.gameRenderer.checkEntityPostEffect(this.cameraEntity);
        this.grayscaled = false;
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
