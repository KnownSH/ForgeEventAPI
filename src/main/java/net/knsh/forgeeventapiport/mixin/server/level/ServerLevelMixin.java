package net.knsh.forgeeventapiport.mixin.server.level;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.forgeeventapiport.accessors.ForgeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Shadow public abstract void removePlayerImmediately(ServerPlayer player, Entity.RemovalReason reason);

    @Inject(
            method = "addPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z")
    )
    private void neoforged$onAddPlayer(ServerPlayer player, CallbackInfo ci) {
        ((ForgeEntity) player).onAddedToWorld();
    }

    @ModifyReturnValue(
            method = "addEntity",
            at = @At(value = "RETURN", ordinal = 1)
    )
    private boolean neoforged$onAddEntity(boolean original, @Local Entity entity) {
        if (original) {
            ((ForgeEntity) entity).onAddedToWorld();
            return true;
        } else {
            return false;
        }
    }

    @Inject(
            method = "updateNeighborsAt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/redstone/NeighborUpdater;updateNeighborsAtExceptFromFacing(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/Direction;)V")
    )
    private void neoforged$onUpdateNeighborsAt(BlockPos pos, Block blockType, CallbackInfo ci) {
        EventHooks.onNeighborNotify((ServerLevel) (Object) this, pos, this.getBlockState(pos), EnumSet.allOf(Direction.class), false).isCanceled();
    }

    @Inject(
            method = "updateNeighborsAtExceptFromFacing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/redstone/NeighborUpdater;updateNeighborsAtExceptFromFacing(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/Direction;)V")
    )
    private void neoforged$onUpdateNeighborsAtExceptFromFacing(BlockPos pos, Block blockType, Direction skipSide, CallbackInfo ci) {
        EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
        directions.remove(skipSide);
        if (EventHooks.onNeighborNotify((ServerLevel) (Object) this, pos, this.getBlockState(pos), directions, false).isCanceled()) {
            ci.cancel();
        }
    }
}
