package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimeS2CPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "initializeConnectionToPlayer", at = @At("TAIL"), remap = false)
    private void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn, NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci) {
        ((EntityPlayerInvoker) playerIn).setLoggedIn(true);
        WorldInvoker invoker = WorldInvoker.invoker(playerIn.world);
        if (invoker.timeStopping()) {
            com.hamusuke.theworld.network.NetworkManager.sendToClient(new THE_WORLDStopsTimeS2CPacket(invoker.getStopper()), playerIn);
        }
    }

    @Inject(method = "playerLoggedOut", at = @At("HEAD"))
    private void playerLoggedOut(EntityPlayerMP playerIn, CallbackInfo ci) {
        WorldInvoker invoker = WorldInvoker.invoker(playerIn.world);
        if (THE_WORLDUtil.isEntityStopper(playerIn.world, playerIn)) {
            invoker.startTime(playerIn);
        }
    }
}
