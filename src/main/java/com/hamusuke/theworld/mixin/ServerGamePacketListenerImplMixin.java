package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.invoker.LevelInvoker;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket.Handler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;

    public ServerGamePacketListenerImplMixin(MinecraftServer p_299469_, Connection p_300872_, CommonListenerCookie p_300277_) {
        super(p_299469_, p_300872_, p_300277_);
    }

    @Redirect(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;dispatch(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket$Handler;)V"))
    private void handleInteract$dispatch(ServerboundInteractPacket instance, Handler p_179618_) {
        if (!LevelInvoker.stopping(this.player.serverLevel())) {
            instance.dispatch(p_179618_);
            return;
        }

        var serverLevel = this.player.serverLevel();
        var entity = instance.getTarget(this.player.serverLevel());
        if (entity == null) {
            return;
        }

        instance.dispatch(new Handler() {
            @Override
            public void onInteraction(InteractionHand interactionHand) {
                p_179618_.onInteraction(interactionHand);
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 vec3) {
                p_179618_.onInteraction(interactionHand, vec3);
            }

            @Override
            public void onAttack() {
                if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb) && entity != ServerGamePacketListenerImplMixin.this.player) {
                    ItemStack itemstack = ServerGamePacketListenerImplMixin.this.player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (itemstack.isItemEnabled(serverLevel.enabledFeatures())) {
                        ServerGamePacketListenerImplMixin.this.player.attack(entity);
                    }
                } else {
                    p_179618_.onAttack();
                }
            }
        });
    }
}
