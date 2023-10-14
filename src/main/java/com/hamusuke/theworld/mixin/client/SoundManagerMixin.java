package com.hamusuke.theworld.mixin.client;

import com.google.common.collect.Multimap;
import com.hamusuke.theworld.THE_WORLD;
import com.hamusuke.theworld.invoker.SoundManagerInvoker;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin implements SoundManagerInvoker {
    @Shadow
    private boolean loaded;

    @Shadow
    @Final
    private Map<String, ISound> playingSounds;
    @Shadow
    @Final
    private List<String> pausedChannels;
    @Shadow
    @Final
    private Multimap<SoundCategory, String> categorySounds;

    @Shadow
    @Final
    private Map<String, Integer> playingSoundsStopTime;

    @Shadow
    public abstract void stop(String p_189567_1_, SoundCategory p_189567_2_);

    @Override
    public void stopTHE_WORLDSounds() {
        if (this.loaded) {
            List<String> soundsToBeStopped = this.playingSounds.entrySet().stream().filter(e -> e.getValue().getSoundLocation().equals(THE_WORLD.THE_WORLD_ID) || e.getValue().getSoundLocation().equals(THE_WORLD.THE_WORLD_RELEASED_ID)).map(Map.Entry::getKey).collect(Collectors.toList());
            for (String s : soundsToBeStopped) {
                this.stop(s, null);
            }

            this.pausedChannels.removeAll(soundsToBeStopped);
            soundsToBeStopped.forEach(this.playingSounds::remove);
            this.categorySounds.values().removeAll(soundsToBeStopped);
            soundsToBeStopped.forEach(this.playingSoundsStopTime::remove);
        }
    }
}
