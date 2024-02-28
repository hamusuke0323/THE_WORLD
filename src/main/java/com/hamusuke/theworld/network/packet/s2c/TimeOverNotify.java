package com.hamusuke.theworld.network.packet.s2c;

import com.hamusuke.theworld.TheWorld;
import com.hamusuke.theworld.TheWorldUtil;
import com.hamusuke.theworld.invoker.LevelInvoker;
import com.hamusuke.theworld.invoker.MinecraftInvoker;
import com.hamusuke.theworld.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public class TimeOverNotify implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            var mcInvoker = MinecraftInvoker.invoker(mc);
            if (mc.level == null) {
                return;
            }

            var invoker = LevelInvoker.invoker(mc.level);
            var stopper = invoker.getStopper();

            invoker.startTime(stopper);
            mc.tell(mcInvoker::unloadGrayscaleShader);
            mc.tell(mc.getSoundManager()::resume);

            if (TheWorldUtil.amIStopper(stopper)) {
                MinecraftInvoker.invoker(mc).finishNPInverse();
                TheWorldUtil.playTheWorldSE(stopper, TheWorld.THE_WORLD_RELEASED_ID);
            }
        }));

        return true;
    }
}
