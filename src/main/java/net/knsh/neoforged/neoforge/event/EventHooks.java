package net.knsh.neoforged.neoforge.event;

import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.knsh.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.knsh.neoforged.neoforge.event.level.ExplosionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EventHooks {
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
