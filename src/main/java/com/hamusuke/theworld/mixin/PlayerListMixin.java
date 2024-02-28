package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.PlayerInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.TimeStopsNotify;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"), remap = false)
    private void initializeConnectionToPlayer(Connection p_11262_, ServerPlayer p_11263_, CommonListenerCookie p_297215_, CallbackInfo ci) {
        PlayerInvoker.invoker(p_11263_).setLoggedIn(true);
        LevelInvoker invoker = LevelInvoker.invoker(p_11263_.level());
        if (invoker.timeStopping()) {
            NetworkManager.sendToClient(new TimeStopsNotify(invoker.getStopper()), p_11263_);
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void playerLoggedOut(ServerPlayer p_11287_, CallbackInfo ci) {
        LevelInvoker invoker = LevelInvoker.invoker(p_11287_.level());
        if (TheWorldUtil.isEntityStopper(p_11287_.level(), p_11287_)) {
            invoker.startTime(p_11287_);
        }
    }
}
