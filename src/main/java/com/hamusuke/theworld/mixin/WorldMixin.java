package com.hamusuke.theworld.mixin;

import com.hamusuke.theworld.THE_WORLDUtil;
import com.hamusuke.theworld.config.CommonConfig;
import com.hamusuke.theworld.invoker.EntityLivingBaseInvoker;
import com.hamusuke.theworld.invoker.EntityLivingInvoker;
import com.hamusuke.theworld.invoker.EntityPlayerInvoker;
import com.hamusuke.theworld.invoker.WorldInvoker;
import com.hamusuke.theworld.network.NetworkManager;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDStopsTimePacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDSuccessPacket;
import com.hamusuke.theworld.network.packet.s2c.THE_WORLDTimeOverPacket;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(World.class)
public abstract class WorldMixin implements WorldInvoker {
    @Shadow
    @Final
    public boolean isRemote;

    @Shadow
    @javax.annotation.Nullable
    public abstract MinecraftServer getMinecraftServer();

    @Shadow
    @Final
    public WorldProvider provider;
    @Shadow
    @Final
    public Profiler profiler;
    @Shadow
    protected IChunkProvider chunkProvider;

    @Shadow
    public abstract int calculateSkylightSubtracted(float partialTicks);

    @Shadow
    public abstract int getSkylightSubtracted();

    @Shadow
    public abstract void setSkylightSubtracted(int newSkylightSubtracted);

    @Shadow
    public abstract void updateEntity(Entity ent);

    @Shadow
    protected abstract void tickPlayers();

    @Shadow
    @Final
    public List<Entity> loadedEntityList;

    @Shadow
    public abstract boolean tickUpdates(boolean runAllPending);

    @Shadow
    @Final
    protected List<Entity> unloadedEntityList;

    @Shadow
    protected abstract boolean isChunkLoaded(int x, int z, boolean allowEmpty);

    @Shadow
    public abstract Chunk getChunkFromChunkCoords(int chunkX, int chunkZ);

    @Shadow
    public abstract void onEntityRemoved(Entity entityIn);

    @Shadow
    public abstract void removeEntity(Entity entityIn);

    @Shadow
    public abstract void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate);

    @Shadow
    @Final
    public List<EntityPlayer> playerEntities;
    @Unique
    protected boolean timeStopping;
    @Unique
    protected int timeLimitTicks;
    @Unique
    protected EntityPlayer stopper;

    @Override
    public synchronized void stopTime(EntityPlayer stopper) {
        if (this.timeStopping || stopper == null) {
            return;
        }

        if (!this.isRemote && (!stopper.isCreative() && !((EntityPlayerInvoker) stopper).canTHE_WORLD())) {
            return;
        }

        this.stopper = stopper;
        this.timeStopping = true;
        if (!this.isRemote) {
            NetworkManager.sendToClient(new THE_WORLDSuccessPacket(), (EntityPlayerMP) this.stopper);
            if (CommonConfig.allowFlyWhenTimeStopping && !this.stopper.capabilities.allowFlying) {
                this.stopper.capabilities.allowFlying = true;
                this.stopper.sendPlayerAbilities();
            }
            NetworkManager.sendToDimension(new THE_WORLDStopsTimePacket(this.stopper), this.getMinecraftServer(), this.provider.getDimension());
        }
    }

    @Override
    public synchronized void startTime(EntityPlayer releaser) {
        if (!this.timeStopping || (releaser != null && !releaser.equals(this.stopper))) {
            return;
        }

        EntityPlayerInvoker stopper = (EntityPlayerInvoker) this.stopper;
        this.timeStopping = false;
        if (!this.isRemote) {
            ((EntityPlayerMP) stopper).interactionManager.getGameType().configurePlayerCapabilities(this.stopper.capabilities);
            this.stopper.sendPlayerAbilities();
            NetworkManager.sendToDimension(new THE_WORLDTimeOverPacket(), this.getMinecraftServer(), this.provider.getDimension());
            stopper.setCoolDownTicks(THE_WORLDUtil.getAdjustedCoolDown(this.timeLimitTicks));
        }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true)
    private void updateEntities(CallbackInfo ci) {
        if (this.timeStopping) {
            if (this.stopper != null && ((EntityPlayerInvoker) this.stopper).isInEffect()) {
                ci.cancel();
                return;
            }

            this.profiler.startSection("entities");
            this.profiler.startSection("remove");
            this.loadedEntityList.removeAll(this.unloadedEntityList);

            for (Entity entity1 : this.unloadedEntityList) {
                int j = entity1.chunkCoordX;
                int k1 = entity1.chunkCoordZ;

                if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true)) {
                    this.getChunkFromChunkCoords(j, k1).removeEntity(entity1);
                }
            }

            for (Entity value : this.unloadedEntityList) {
                this.onEntityRemoved(value);
            }

            this.unloadedEntityList.clear();
            this.tickPlayers();
            this.profiler.endStartSection("regular");
            for (int i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
                Entity entity = this.loadedEntityList.get(i1);
                Entity entity1 = entity.getRidingEntity();

                if (entity1 != null) {
                    if (!entity1.isDead && entity1.isPassenger(entity)) {
                        continue;
                    }

                    entity.dismountRidingEntity();
                }

                this.profiler.startSection("tick");
                if (!entity.isDead && !(entity instanceof EntityPlayerMP)) {
                    try {
                        if (THE_WORLDUtil.updatableInStoppedTime(this, entity) && !entity.updateBlocked) {
                            net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(entity);
                            this.updateEntity(entity);
                            net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(entity);
                            continue;
                        } else if (THE_WORLDUtil.movableInStoppedTime(this, entity) && !entity.updateBlocked) {
                            ((EntityLiving) entity).onLivingUpdate();
                            EntityLiving living = (EntityLiving) entity;

                            this.updateEntityWithOptionalForce(entity, false);

                            if (!entity.world.isRemote) {
                                ((EntityLivingInvoker) living).updateLeashedStateV();
                            }
                        }

                        if (entity.hurtResistantTime > 0 && !(entity instanceof EntityPlayer)) {
                            --entity.hurtResistantTime;
                        }

                        if (entity instanceof EntityLivingBase) {
                            ((EntityLivingBaseInvoker) entity).collideWithNearbyEntitiesV();
                        }
                    } catch (Throwable throwable1) {
                        CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                        CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
                        entity.addEntityCrashInfo(crashreportcategory1);
                        if (net.minecraftforge.common.ForgeModContainer.removeErroringEntities) {
                            net.minecraftforge.fml.common.FMLLog.log.fatal("{}", crashreport1.getCompleteReport());
                            removeEntity(entity);
                        } else
                            throw new ReportedException(crashreport1);
                    }
                }

                this.profiler.endSection();
                this.profiler.startSection("remove");

                if (entity.isDead) {
                    int l1 = entity.chunkCoordX;
                    int i2 = entity.chunkCoordZ;

                    if (entity.addedToChunk && this.isChunkLoaded(l1, i2, true)) {
                        this.getChunkFromChunkCoords(l1, i2).removeEntity(entity);
                    }

                    this.loadedEntityList.remove(i1--);
                    this.onEntityRemoved(entity);
                }

                this.profiler.endSection();
            }

            this.profiler.endSection();

            ci.cancel();
        }
    }

    @Override
    public EntityPlayer getStopper() {
        return this.stopper;
    }

    @Override
    public boolean timeStopping() {
        return this.timeStopping;
    }

    @Override
    public void setTimeLimitTicks(int ticks) {
        this.timeLimitTicks = ticks;
    }
}
