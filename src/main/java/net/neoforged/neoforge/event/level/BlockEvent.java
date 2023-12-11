package net.neoforged.neoforge.event.level;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.event.Event;
import net.knsh.forgeeventapiport.accessors.ForgeBlockState;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;

public abstract class BlockEvent extends ForgeEvent {
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("neoforge.debugBlockEvent", "false"));

    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;

    public BlockEvent(LevelAccessor level, BlockPos pos, BlockState state) {
        this.pos = pos;
        this.level = level;
        this.state = state;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public static class BreakEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<IBreakEvent> EVENT = ForgeEventFactory.create(IBreakEvent.class, (listeners) -> (event) -> {
            for (IBreakEvent listener : listeners) {
                event = listener.onBreakEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IBreakEvent {
            BreakEvent onBreakEvent(BreakEvent event);
        }

        private final Player player;
        private int exp;

        public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
            super(level, pos, state);
            this.player = player;

            if (state == null || !CommonHooks.isCorrectToolForDrops(state, player)) // Handle empty block or player unable to break block scenario
            {
                this.exp = 0;
            } else {
                int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
                int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
                this.exp = ((ForgeBlockState) state).getExpDrop(level, level.random, pos, fortuneLevel, silkTouchLevel);
            }
        }

        public Player getPlayer() {
            return player;
        }

        /**
         * Get the experience dropped by the block after the event has processed
         *
         * @return The experience to drop or 0 if the event was canceled
         */
        public int getExpToDrop() {
            return this.isCanceled() ? 0 : exp;
        }

        /**
         * Set the amount of experience dropped by the block after the event has processed
         *
         * @param exp 1 or higher to drop experience, else nothing will drop
         */
        public void setExpToDrop(int exp) {
            this.exp = exp;
        }
    }

    /**
     * Called when a block is placed.
     *
     * If a Block Place event is cancelled, the block will not be placed.
     */
    public static class EntityPlaceEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<IEntityPlaceEvent> EVENT = ForgeEventFactory.create(IEntityPlaceEvent.class, (listeners) -> (event) -> {
            for (IEntityPlaceEvent listener : listeners) {
                event = listener.onBreakEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IEntityPlaceEvent {
            EntityPlaceEvent onBreakEvent(EntityPlaceEvent event);
        }

        private final Entity entity;
        private final BlockSnapshot blockSnapshot;
        private final BlockState placedBlock;
        private final BlockState placedAgainst;

        public EntityPlaceEvent(@NotNull BlockSnapshot blockSnapshot, @NotNull BlockState placedAgainst, @Nullable Entity entity) {
            super(blockSnapshot.getLevel(), blockSnapshot.getPos(), !(entity instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock());
            this.entity = entity;
            this.blockSnapshot = blockSnapshot;
            this.placedBlock = !(entity instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
            this.placedAgainst = placedAgainst;

            if (DEBUG) {
                System.out.printf("Created EntityPlaceEvent - [PlacedBlock: %s ][PlacedAgainst: %s ][Entity: %s ]\n", getPlacedBlock(), placedAgainst, entity);
            }
        }

        @Nullable
        public Entity getEntity() {
            return entity;
        }

        public BlockSnapshot getBlockSnapshot() {
            return blockSnapshot;
        }

        public BlockState getPlacedBlock() {
            return placedBlock;
        }

        public BlockState getPlacedAgainst() {
            return placedAgainst;
        }
    }

    /**
     * Fired when a single block placement triggers the
     * creation of multiple blocks(e.g. placing a bed block). The block returned
     * by {@link #state} and its related methods is the block where
     * the placed block would exist if the placement only affected a single
     * block.
     */
    public static class EntityMultiPlaceEvent extends EntityPlaceEvent implements ICancellableEvent {
        public static final Event<IEntityMultiPlaceEvent> EVENT = ForgeEventFactory.create(IEntityMultiPlaceEvent.class, (listeners) -> (event) -> {
            for (IEntityMultiPlaceEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IEntityMultiPlaceEvent {
            EntityMultiPlaceEvent onEvent(EntityMultiPlaceEvent event);
        }

        private final List<BlockSnapshot> blockSnapshots;

        public EntityMultiPlaceEvent(@NotNull List<BlockSnapshot> blockSnapshots, @NotNull BlockState placedAgainst, @Nullable Entity entity) {
            super(blockSnapshots.get(0), placedAgainst, entity);
            this.blockSnapshots = ImmutableList.copyOf(blockSnapshots);
            if (DEBUG) {
                System.out.printf("Created EntityMultiPlaceEvent - [PlacedAgainst: %s ][Entity: %s ]\n", placedAgainst, entity);
            }
        }

        /**
         * Gets a list of BlockSnapshots for all blocks which were replaced by the
         * placement of the new blocks. Most of these blocks will just be of type AIR.
         *
         * @return immutable list of replaced BlockSnapshots
         */
        public List<BlockSnapshot> getReplacedBlockSnapshots() {
            return blockSnapshots;
        }
    }

    /**
     * Fired when a physics update occurs on a block. This event acts as
     * a way for mods to detect physics updates, in the same way a BUD switch
     * does. This event is only called on the server.
     */
    public static class NeighborNotifyEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<INeighborNotifyEvent> EVENT = ForgeEventFactory.create(INeighborNotifyEvent.class, (listeners) -> (event) -> {
            for (INeighborNotifyEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface INeighborNotifyEvent {
            NeighborNotifyEvent onEvent(NeighborNotifyEvent event);
        }

        private final EnumSet<Direction> notifiedSides;
        private final boolean forceRedstoneUpdate;

        public NeighborNotifyEvent(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
            super(level, pos, state);
            this.notifiedSides = notifiedSides;
            this.forceRedstoneUpdate = forceRedstoneUpdate;
        }

        /**
         * Gets a list of directions from the base block that updates will occur upon.
         *
         * @return list of notified directions
         */
        public EnumSet<Direction> getNotifiedSides() {
            return notifiedSides;
        }

        /**
         * Get if redstone update was forced during setBlock call (0x16 to flags)
         *
         * @return if the flag was set
         */
        public boolean getForceRedstoneUpdate() {
            return forceRedstoneUpdate;
        }
    }

    /**
     * Fired when a liquid places a block. Use {@link #setNewState(BlockState)} to change the result of
     * a cobblestone generator or add variants of obsidian. Alternatively, you could execute
     * arbitrary code when lava sets blocks on fire, even preventing it.
     *
     * {@link #getState()} will return the block that was originally going to be placed.
     * {@link #getPos()} will return the position of the block to be changed.
     */
    public static class FluidPlaceBlockEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<IFluidPlaceBlockEvent> EVENT = ForgeEventFactory.create(IFluidPlaceBlockEvent.class, (listeners) -> (event) -> {
            for (IFluidPlaceBlockEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IFluidPlaceBlockEvent {
            FluidPlaceBlockEvent onEvent(FluidPlaceBlockEvent event);
        }

        private final BlockPos liquidPos;
        private BlockState newState;
        private BlockState origState;

        public FluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
            super(level, pos, state);
            this.liquidPos = liquidPos;
            this.newState = state;
            this.origState = level.getBlockState(pos);
        }

        /**
         * @return The position of the liquid this event originated from. This may be the same as {@link #getPos()}.
         */
        public BlockPos getLiquidPos() {
            return liquidPos;
        }

        /**
         * @return The block state that will be placed after this event resolves.
         */
        public BlockState getNewState() {
            return newState;
        }

        public void setNewState(BlockState state) {
            this.newState = state;
        }

        /**
         * @return The state of the block to be changed before the event was fired.
         */
        public BlockState getOriginalState() {
            return origState;
        }
    }

    /**
     * Fired when a crop block grows. See subevents.
     *
     */
    public static abstract class CropGrowEvent extends BlockEvent {
        public CropGrowEvent(Level level, BlockPos pos, BlockState state) {
            super(level, pos, state);
        }

        /**
         * Fired when any "growing age" blocks (for example cacti, chorus plants, or crops
         * in vanilla) attempt to advance to the next growth age state during a random tick.<br>
         * <br>
         * {@link Result#DEFAULT} will pass on to the vanilla growth mechanics.<br>
         * {@link Result#ALLOW} will force the plant to advance a growth stage.<br>
         * {@link Result#DENY} will prevent the plant from advancing a growth stage.<br>
         * <br>
         * This event is not {@link ICancellableEvent}.<br>
         * <br>
         */
        public static class Pre extends CropGrowEvent {
            public static final Event<IPreCropGrowEvent> EVENT = ForgeEventFactory.create(IPreCropGrowEvent.class, (listeners) -> (event) -> {
                for (IPreCropGrowEvent listener : listeners) {
                    event = listener.onEvent(event);
                }
                return event;
            });

            public static void onSubscription(Method method, Object object, ResourceLocation priority) {
                EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
            }

            @FunctionalInterface
            public interface IPreCropGrowEvent {
                Pre onEvent(Pre event);
            }

            public Pre(Level level, BlockPos pos, BlockState state) {
                super(level, pos, state);
            }
        }

        /**
         * Fired when "growing age" blocks (for example cacti, chorus plants, or crops
         * in vanilla) have successfully grown. The block's original state is available,
         * in addition to its new state.<br>
         * <br>
         * This event is not {@link ICancellableEvent}.<br>
         * <br>
         */
        public static class Post extends CropGrowEvent {
            public static final Event<IPostCropGrowEvent> EVENT = ForgeEventFactory.create(IPostCropGrowEvent.class, (listeners) -> (event) -> {
                for (IPostCropGrowEvent listener : listeners) {
                    event = listener.onEvent(event);
                }
                return event;
            });

            public static void onSubscription(Method method, Object object, ResourceLocation priority) {
                EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
            }

            @FunctionalInterface
            public interface IPostCropGrowEvent {
                Post onEvent(Post event);
            }

            private final BlockState originalState;

            public Post(Level level, BlockPos pos, BlockState original, BlockState state) {
                super(level, pos, state);
                originalState = original;
            }

            public BlockState getOriginalState() {
                return originalState;
            }
        }
    }

    /**
     * Fired when when farmland gets trampled
     * This event is {@link ICancellableEvent}
     */
    public static class FarmlandTrampleEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<IFarmlandTrampleEvent> EVENT = ForgeEventFactory.create(IFarmlandTrampleEvent.class, (listeners) -> (event) -> {
            for (IFarmlandTrampleEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IFarmlandTrampleEvent {
            FarmlandTrampleEvent onEvent(FarmlandTrampleEvent event);
        }

        private final Entity entity;
        private final float fallDistance;

        public FarmlandTrampleEvent(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
            super(level, pos, state);
            this.entity = entity;
            this.fallDistance = fallDistance;
        }

        public Entity getEntity() {
            return entity;
        }

        public float getFallDistance() {
            return fallDistance;
        }

    }

    /**
     * Fired when an attempt is made to spawn a nether portal from
     * {@link net.minecraft.world.level.block.BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.
     *
     * If cancelled, the portal will not be spawned.
     */
    public static class PortalSpawnEvent extends BlockEvent implements ICancellableEvent {
        public static final Event<IPortalSpawnEvent> EVENT = ForgeEventFactory.create(IPortalSpawnEvent.class, (listeners) -> (event) -> {
            for (IPortalSpawnEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPortalSpawnEvent {
            PortalSpawnEvent onEvent(PortalSpawnEvent event);
        }

        private final PortalShape size;

        public PortalSpawnEvent(LevelAccessor level, BlockPos pos, BlockState state, PortalShape size) {
            super(level, pos, state);
            this.size = size;
        }

        public PortalShape getPortalSize() {
            return size;
        }
    }
}
