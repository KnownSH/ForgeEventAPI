package net.knsh.neoforged.neoforge.event;

import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.neoforge.common.ToolAction;
import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;
import net.knsh.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.knsh.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.knsh.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.knsh.neoforged.neoforge.event.level.BlockEvent;
import net.knsh.neoforged.neoforge.event.level.ExplosionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class EventHooks {
    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
        BlockSnapshot snap = blockSnapshots.get(0);
        BlockState placedAgainst = snap.getLevel().getBlockState(snap.getPos().relative(direction.getOpposite()));
        BlockEvent.EntityMultiPlaceEvent event = new BlockEvent.EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return BlockEvent.EntityMultiPlaceEvent.EVENT.invoker().onEvent(event).isCanceled();
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @NotNull BlockSnapshot blockSnapshot, @NotNull Direction direction) {
        BlockState placedAgainst = blockSnapshot.getLevel().getBlockState(blockSnapshot.getPos().relative(direction.getOpposite()));
        BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return BlockEvent.EntityPlaceEvent.EVENT.invoker().onBreakEvent(event).isCanceled();
    }

    public static BlockEvent.NeighborNotifyEvent onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
        BlockEvent.NeighborNotifyEvent event = new BlockEvent.NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate);
        event = BlockEvent.NeighborNotifyEvent.EVENT.invoker().onEvent(event);
        return event;
    }

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state);
        event = BlockEvent.FluidPlaceBlockEvent.EVENT.invoker().onEvent(event);
        return event.getNewState();
    }

    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        event = PlayerEvent.HarvestCheck.EVENT.invoker().onHarvestCheck(event);
        return event.canHarvest();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        if (LivingExperienceDropEvent.EVENT.invoker().onLivingExperienceDrop(event).isCanceled()) {
            return 0;
        }
        return event.getDroppedExperience();
    }

    public static Optional<PortalShape> onTrySpawnPortal(LevelAccessor level, BlockPos pos, Optional<PortalShape> size) {
        if (!size.isPresent()) return size;
        return BlockEvent.PortalSpawnEvent.EVENT.invoker().onEvent(new BlockEvent.PortalSpawnEvent(level, pos, level.getBlockState(pos), size.get())).isCanceled() ? size : Optional.empty();
    }

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return ExplosionEvent.Start.EVENT.invoker().onStartEvent(new ExplosionEvent.Start(level, explosion)).isCanceled();
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        ExplosionEvent.Detonate.EVENT.invoker().onDetonateEvent(new ExplosionEvent.Detonate(level, explosion, list));
    }

    @ApiStatus.Internal
    public static boolean checkSpawnPlacements(EntityType<?> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, boolean defaultResult) {
        var event = new MobSpawnEvent.SpawnPlacementCheck(entityType, level, spawnType, pos, random, defaultResult);
        event = MobSpawnEvent.SpawnPlacementCheck.EVENT.invoker().onSpawnPlacementCheck(event);
        return event.getResult() == ForgeEvent.Result.DEFAULT ? defaultResult : event.getResult() == ForgeEvent.Result.ALLOW;
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, level, charge, hasAmmo);
        if (ArrowLooseEvent.EVENT.invoker().onEvent(event).isCanceled())
            return -1;
        return event.getCharge();
    }

    public static boolean checkSpawnPosition(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType) {
        var event = new MobSpawnEvent.PositionCheck(mob, level, spawnType, null);
        event = MobSpawnEvent.PositionCheck.EVENT.invoker().onPositionCheck(event);
        if (event.getResult() == ForgeEvent.Result.DEFAULT) {
            return mob.checkSpawnRules(level, spawnType) && mob.checkSpawnObstruction(level);
        }
        return event.getResult() == ForgeEvent.Result.ALLOW;
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        var event = new MobSpawnEvent.PositionCheck(mob, level, spawnType, null);
        event = MobSpawnEvent.PositionCheck.EVENT.invoker().onPositionCheck(event);
        if (event.getResult() == ForgeEvent.Result.DEFAULT) {
            // Spawners do not evaluate Mob#checkSpawnRules if any custom rules are present. This is despite the fact that these two methods do not check the same things.
            return (spawnData.getCustomSpawnRules().isPresent() || mob.checkSpawnRules(level, spawnType)) && mob.checkSpawnObstruction(level);
        }
        return event.getResult() == ForgeEvent.Result.ALLOW;
    }

    @Nullable
    @SuppressWarnings("deprecation") // Call to deprecated Mob#finalizeSpawn is expected.
    public static SpawnGroupData onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, spawnType, spawnData, spawnTag, null);
        boolean cancel = MobSpawnEvent.FinalizeSpawn.EVENT.invoker().onFinalizeSpawn(event).isCanceled();

        if (!cancel) {
            mob.finalizeSpawn(level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
        }

        return cancel ? null : event.getSpawnData();
    }

    @Nullable
    public static MobSpawnEvent.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, MobSpawnType.SPAWNER, spawnData, spawnTag, spawner);
        boolean cancel = MobSpawnEvent.FinalizeSpawn.EVENT.invoker().onFinalizeSpawn(event).isCanceled();
        return cancel ? null : event;
    }

    public static ForgeEvent.Result canEntityDespawn(Mob entity, ServerLevelAccessor level) {
        MobSpawnEvent.AllowDespawn event = new MobSpawnEvent.AllowDespawn(entity, level);
        event = MobSpawnEvent.AllowDespawn.EVENT.invoker().onAllowDespawn(event);
        return event.getResult();
    }
}
