package net.knsh.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.accessors.ForgeMob;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.knsh.neoforged.neoforge.event.entity.EntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public abstract class MobSpawnEvent extends EntityEvent {
    private final ServerLevelAccessor level;
    private final double x;
    private final double y;
    private final double z;

    @ApiStatus.Internal
    protected MobSpawnEvent(Mob mob, ServerLevelAccessor level, double x, double y, double z) {
        super(mob);
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Mob getEntity() {
        return (Mob) super.getEntity();
    }

    /**
     * @return The level relating to the mob spawn action
     */
    public ServerLevelAccessor getLevel() {
        return this.level;
    }

    /**
     * @return The x-coordinate relating to the mob spawn action
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return The y-coordinate relating to the mob spawn action
     */
    public double getY() {
        return this.y;
    }

    /**
     * @return The z-coordinate relating to the mob spawn action
     */
    public double getZ() {
        return this.z;
    }

    /**
     * This event is fired {@linkplain net.minecraft.world.entity.SpawnPlacements#checkSpawnRules when Spawn Placements (aka Spawn Rules) are checked}, before a mob attempts to spawn.<br>
     * Spawn Placement checks include light levels, slime chunks, grass blocks for animals, and others in the same vein.<br>
     * The purpose of this event is to permit runtime changes to any or all spawn placement logic without having to wrap the placement for each entity.
     * <p>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the spawn process will continue.</li>
     * <li>Default - The value of the vanilla check will be used to determine success.</li>
     * <li>Deny - The check will fail, and the spawn process will abort.</li>
     * </ul>
     * This event is fired on the {@linkplain net.knsh.neoforged.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#SERVER logical server}.
     * <p>
     * This event is not fired for mob spawners which utilize {@link net.minecraft.world.level.SpawnData.CustomSpawnRules}, as they do not check spawn placements.
     *
     * @apiNote If your modifications are for a single entity, and do not vary at runtime, use {@link //SpawnPlacementRegisterEvent}.
     * @see //SpawnPlacementRegisterEvent
     */
    public static class SpawnPlacementCheck extends ForgeEvent {
        @ApiStatus.Internal
        public static final Event<ISpawnPlacementCheck> EVENT = ForgeEventFactory.create(ISpawnPlacementCheck.class, (listeners) -> (event) -> {
            for (ISpawnPlacementCheck listener : listeners) {
                event = listener.onSpawnPlacementCheck(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ISpawnPlacementCheck {
            SpawnPlacementCheck onSpawnPlacementCheck(SpawnPlacementCheck event);
        }

        private final EntityType<?> entityType;
        private final ServerLevelAccessor level;
        private final MobSpawnType spawnType;
        private final BlockPos pos;
        private final RandomSource random;
        private final boolean defaultResult;

        /**
         * Internal.
         *
         * @see {@link net.minecraft.world.entity.SpawnPlacements#checkSpawnRules} for the single call site of this event.
         */
        @ApiStatus.Internal
        public SpawnPlacementCheck(EntityType<?> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, boolean defaultResult) {
            this.entityType = entityType;
            this.level = level;
            this.spawnType = spawnType;
            this.pos = pos;
            this.random = random;
            this.defaultResult = defaultResult;
        }

        /**
         * @return The type of entity that checks are being performed for.
         */
        public EntityType<?> getEntityType() {
            return this.entityType;
        }

        /**
         * @return The level relating to the mob spawn action
         */
        public ServerLevelAccessor getLevel() {
            return this.level;
        }

        /**
         * Retrieves the type of mob spawn that is happening.
         *
         * @return The mob spawn type.
         * @see MobSpawnType
         */
        public MobSpawnType getSpawnType() {
            return this.spawnType;
        }

        /**
         * @return The position where checks are being evaluated.
         */
        public BlockPos getPos() {
            return this.pos;
        }

        /**
         * In all vanilla cases, this is equal to {@link ServerLevelAccessor#getRandom()}.
         *
         * @return The random source being used.
         */
        public RandomSource getRandom() {
            return this.random;
        }

        /**
         * The default vanilla result is useful if an additional check wants to force {@link Result#ALLOW} only if the vanilla check would succeed.
         *
         * @return The result of the vanilla spawn placement check.
         */
        public boolean getDefaultResult() {
            return this.defaultResult;
        }
    }

    /**
     * This event is fired when a mob checks for a valid spawn position, after {@link net.minecraft.world.entity.SpawnPlacements#checkSpawnRules} has been evaluated.<br>
     * Conditions validated here include the following:
     * <ul>
     * <li>Obstruction - mobs inside blocks or fluids.</li>
     * <li>Pathfinding - if the spawn block is valid for pathfinding.</li>
     * <li>Sea Level - Ocelots check if the position is above sea level.</li>
     * <li>Spawn Block - Ocelots check if the below block is grass or leaves.</li>
     * </ul>
     * <p>
     * These checks are performed by the vanilla methods {@link Mob#checkSpawnRules} and {@link Mob#checkSpawnObstruction}.<br>
     * The logical-and of both methods forms the default result of this event.
     * <p>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The position will be accepted, and the spawn process will continue.</li>
     * <li>Default - The position will be accepted if {@link Mob#checkSpawnRules} and {@link Mob#checkSpawnObstruction} are both true.</li>
     * <li>Deny - The position will not be accepted. The spawn process will abort, and further events will not be called.</li>
     * </ul>
     * This event is fired on the {@linkplain net.knsh.neoforged.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#SERVER logical server}.
     *
     * @apiNote This event fires after Spawn Placement checks, which are the primary set of spawn checks.
     * @see {@link //SpawnPlacementRegisterEvent} To modify spawn placements statically at startup.
     * @see {@link SpawnPlacementCheck} To modify the result of spawn placements at runtime.
     */
    public static class PositionCheck extends MobSpawnEvent {
        public static final Event<IPositionCheck> EVENT = ForgeEventFactory.create(IPositionCheck.class, (listeners) -> (event) -> {
            for (IPositionCheck listener : listeners) {
                event = listener.onPositionCheck(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPositionCheck {
            PositionCheck onPositionCheck(PositionCheck event);
        }

        @Nullable
        private final BaseSpawner spawner;
        private final MobSpawnType spawnType;

        public PositionCheck(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, @Nullable BaseSpawner spawner) {
            super(mob, level, mob.getX(), mob.getY(), mob.getZ());
            this.spawnType = spawnType;
            this.spawner = spawner;
        }

        /**
         * Retrieves the underlying {@link BaseSpawner} instance if this mob was created by a Mob Spawner of some form.
         * This is always null unless {@link #getSpawnType()} is {@link MobSpawnType#SPAWNER}, and may still be null even then.
         *
         * @return The BaseSpawner responsible for triggering the spawn, or null if none is available.
         */
        @Nullable
        public BaseSpawner getSpawner() {
            return spawner;
        }

        /**
         * Retrieves the type of mob spawn that is happening.
         *
         * @return The mob spawn type.
         * @see MobSpawnType
         */
        public MobSpawnType getSpawnType() {
            return this.spawnType;
        }
    }

    /**
     * This event is fired before {@link Mob#finalizeSpawn} is called.<br>
     * This allows mods to control mob initialization.<br>
     * In vanilla code, this event is injected by a transformer and not via patch, so calls cannot be traced via call hierarchy (it is not source-visible).
     * <p>
     * Canceling this event will result in {@link Mob#finalizeSpawn} not being called, and the returned value always being null, instead of propagating the SpawnGroupData.<br>
     * The entity will still be spawned. If you want to prevent the spawn, use {@link FinalizeSpawn#setSpawnCancelled}, which will cause Forge to prevent the spawn.
     * <p>
     * This event is fired on {@link net.knsh.neoforged.NeoForge#EVENT_BUS}, and is only fired on the logical server.
     *
     * @see net.knsh.neoforged.neoforge.event.EventHooks#onFinalizeSpawn
     * @apiNote Callers do not need to check if the entity's spawn was cancelled, as the spawn will be blocked by Forge.
     */
    public static class FinalizeSpawn extends MobSpawnEvent implements ICancellableEvent {
        public static final Event<IFinalizeSpawn> EVENT = ForgeEventFactory.create(IFinalizeSpawn.class, (listeners) -> (event) -> {
            for (IFinalizeSpawn listener : listeners) {
                event = listener.onFinalizeSpawn(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IFinalizeSpawn {
            FinalizeSpawn onFinalizeSpawn(FinalizeSpawn event);
        }

        private final MobSpawnType spawnType;
        @Nullable
        private final BaseSpawner spawner;

        private DifficultyInstance difficulty;
        @Nullable
        private SpawnGroupData spawnData;
        @Nullable
        private CompoundTag spawnTag;

        /**
         * @apiNote Do not construct directly. Access via {@link net.knsh.neoforged.neoforge.event.EventHooks#onFinalizeSpawn} / {@link net.knsh.neoforged.neoforge.event.EventHooks#onFinalizeSpawnSpawner}.
         */
        @ApiStatus.Internal
        public FinalizeSpawn(Mob entity, ServerLevelAccessor level, double x, double y, double z, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, @Nullable BaseSpawner spawner) {
            super(entity, level, x, y, z);
            this.difficulty = difficulty;
            this.spawnType = spawnType;
            this.spawnData = spawnData;
            this.spawnTag = spawnTag;
            this.spawner = spawner;
        }

        /**
         * Retrieves the {@link DifficultyInstance} for the chunk where the mob is about to be spawned.
         *
         * @return The local difficulty instance
         */
        public DifficultyInstance getDifficulty() {
            return this.difficulty;
        }

        /**
         * Sets the difficulty instance for this event, which will be propagated to {@link Mob#finalizeSpawn} unless cancelled.
         * The difficulty instance controls how likely certain random effects are to occur, or if certain mob abilities are enabled.
         *
         * @param inst The new difficulty instance.
         */
        public void setDifficulty(DifficultyInstance inst) {
            this.difficulty = inst;
        }

        /**
         * Retrieves the type of mob spawn that happened (the event that caused the spawn). The enum names are self-explanatory.
         *
         * @return The mob spawn type.
         * @see MobSpawnType
         */
        public MobSpawnType getSpawnType() {
            return this.spawnType;
        }

        /**
         * Retrieves the {@link SpawnGroupData} for this entity. When spawning mobs in a loop, this group data is used for the entire group and impacts future spawns.
         * This is how entities like horses ensure that the whole group spawns as a single variant. How this is used varies on a per-entity basis.
         *
         * @return The spawn group data.
         */
        @Nullable
        public SpawnGroupData getSpawnData() {
            return this.spawnData;
        }

        /**
         * Sets the spawn data for this entity. If this event is cancelled, this value is not used, since {@link Mob#finalizeSpawn} will not be called.
         *
         * @param data The new spawn data
         * @see FinalizeSpawn#getSpawnData
         */
        public void setSpawnData(@Nullable SpawnGroupData data) {
            this.spawnData = data;
        }

        /**
         * This is the NBT data the entity was loaded from, if applicable. It is unknown if the entity has already been loaded from this data, or if it will be loaded later.
         * Callers should not modify this data. If you need to change the data, you can create a copy, modify it, and set it via {@link FinalizeSpawn#setSpawnTag}
         *
         * @return The spawn data this entity was or will be loaded from, if any.
         */
        @Nullable
        public CompoundTag getSpawnTag() {
            return this.spawnTag;
        }

        /**
         * Sets the spawn data for this event, which will be propagated to {@link Mob#finalizeSpawn} unless cancelled.
         * The only vanilla mob known to use this tag for anything in finalize is tropical fish for setting the variant when spawned via bucket.
         *
         * @param tag The new spawn tag
         */
        public void setSpawnTag(@Nullable CompoundTag tag) {
            this.spawnTag = tag;
        }

        /**
         * Retrieves the underlying {@link BaseSpawner} instance if this mob was created by a Mob Spawner of some form.
         * This is always null unless {@link #getSpawnType()} is {@link MobSpawnType#SPAWNER}, and may still be null even then.
         *
         * @return The BaseSpawner responsible for triggering the spawn, or null if none is available.
         */
        @Nullable
        public BaseSpawner getSpawner() {
            return spawner;
        }

        /**
         * This method can be used to cancel the spawn of this mob.<p>
         * This method must be used if you want to block the spawn, as canceling the event only blocks the call to {@link Mob#finalizeSpawn}.<p>
         * Note that if the spawn is cancelled, but the event is not, then {@link Mob#finalizeSpawn} will still be called, but the entity will not be spawned.
         * Usually that has no side effects, but callers should be aware.
         *
         * @param cancel If the spawn should be cancelled (or not).
         */
        public void setSpawnCancelled(boolean cancel) {
            ((ForgeMob) this.getEntity()).setSpawnCancelled(cancel);
        }

        /**
         * Returns the current spawn cancellation status, which can be changed via {@link FinalizeSpawn#setSpawnCancelled(boolean)}.
         *
         * @return If this mob's spawn is cancelled or not.
         */
        public boolean isSpawnCancelled() {
            return ((ForgeMob) this.getEntity()).isSpawnCancelled();
        }
    }

    /**
     * This event is fired from {@link Mob#checkDespawn()}.<br>
     * It fires once per tick per mob that is attempting to despawn.<br>
     * It is not fired if the mob is persistent (meaning it may not despawn).<br>
     * <p>
     * {@link Result#DEFAULT} indicates that default despawn mechanics should be used.
     * {@link Result#ALLOW} indicates that the mob should forcefully despawn.
     * {@link Result#DENY} indicates that the mob should forcefully stay spawned.
     * <p>
     * This event is fired on the {@linkplain net.knsh.neoforged.NeoForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain net.fabricmc.api.EnvType#SERVER logical server}.
     *
     * @see net.minecraft.world.entity.LivingEntity#checkDespawn()
     * @author cpw
     */
    // TODO: 1.20 Move to standalone class, as it is unrelated to the complex mob spawning flow.
    // Such a refactor will allow the BaseSpawner and MobSpawnType params to be hoisted to MobSpawnEvent.
    public static class AllowDespawn extends MobSpawnEvent {
        public static final Event<IAllowDespawn> EVENT = ForgeEventFactory.create(IAllowDespawn.class, (listeners) -> (event) -> {
            for (IAllowDespawn listener : listeners) {
                event = listener.onAllowDespawn(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IAllowDespawn {
            AllowDespawn onAllowDespawn(AllowDespawn event);
        }

        public AllowDespawn(Mob mob, ServerLevelAccessor level) {
            super(mob, level, mob.getX(), mob.getY(), mob.getZ());
        }
    }
}