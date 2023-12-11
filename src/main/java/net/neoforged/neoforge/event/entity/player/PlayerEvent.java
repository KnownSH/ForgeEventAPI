package net.neoforged.neoforge.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;

public abstract class PlayerEvent extends LivingEvent {
    private final Player player;

    public PlayerEvent(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public static class HarvestCheck extends PlayerEvent {
        public static final Event<IHarvestCheck> EVENT = ForgeEventFactory.create(IHarvestCheck.class, (listeners) -> (event) -> {
            for (IHarvestCheck listener : listeners) {
                event = listener.onHarvestCheck(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IHarvestCheck {
            HarvestCheck onHarvestCheck(HarvestCheck event);
        }

        private final BlockState state;
        private boolean success;

        public HarvestCheck(Player player, BlockState state, boolean success) {
            super(player);
            this.state = state;
            this.success = success;
        }

        public BlockState getTargetBlock() {
            return this.state;
        }

        public boolean canHarvest() {
            return this.success;
        }

        public void setCanHarvest(boolean success) {
            this.success = success;
        }
    }

    public static class BreakSpeed extends PlayerEvent implements ICancellableEvent {
        public static final Event<IBreakSpeed> EVENT = ForgeEventFactory.create(IBreakSpeed.class, (listeners) -> (event) -> {
            for (IBreakSpeed listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IBreakSpeed {
            BreakSpeed onEvent(BreakSpeed event);
        }

        private static final BlockPos LEGACY_UNKNOWN = new BlockPos(0, -1, 0);
        private final BlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final Optional<BlockPos> pos; // Y position of -1 notes unknown location

        public BreakSpeed(Player player, BlockState state, float original, @Nullable BlockPos pos) {
            super(player);
            this.state = state;
            this.originalSpeed = original;
            this.setNewSpeed(original);
            this.pos = Optional.ofNullable(pos);
        }

        public BlockState getState() {
            return state;
        }

        public float getOriginalSpeed() {
            return originalSpeed;
        }

        public float getNewSpeed() {
            return newSpeed;
        }

        public void setNewSpeed(float newSpeed) {
            this.newSpeed = newSpeed;
        }

        public Optional<BlockPos> getPosition() {
            return this.pos;
        }
    }

    public static class NameFormat extends PlayerEvent {
        public static final Event<INameFormat> EVENT = ForgeEventFactory.create(INameFormat.class, (listeners) -> (event) -> {
            for (INameFormat listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface INameFormat {
            NameFormat onEvent(NameFormat event);
        }

        private final Component username;
        private Component displayname;

        public NameFormat(Player player, Component username) {
            super(player);
            this.username = username;
            this.setDisplayname(username);
        }

        public Component getUsername() {
            return username;
        }

        public Component getDisplayname() {
            return displayname;
        }

        public void setDisplayname(Component displayname) {
            this.displayname = displayname;
        }
    }

    public static class TabListNameFormat extends PlayerEvent {
        public static final Event<ITabListNameFormat> EVENT = ForgeEventFactory.create(ITabListNameFormat.class, (listeners) -> (event) -> {
            for (ITabListNameFormat listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ITabListNameFormat {
            TabListNameFormat onEvent(TabListNameFormat event);
        }

        @Nullable
        private Component displayName;

        public TabListNameFormat(Player player) {
            super(player);
        }

        @Nullable
        public Component getDisplayName() {
            return displayName;
        }

        public void setDisplayName(@Nullable Component displayName) {
            this.displayName = displayName;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the impl sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     */
    public static class Clone extends PlayerEvent {
        public static final Event<IClone> EVENT = ForgeEventFactory.create(IClone.class, (listeners) -> (event) -> {
            for (IClone listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IClone {
            Clone onEvent(Clone event);
        }

        private final Player original;
        private final boolean wasDeath;

        public Clone(Player _new, Player oldPlayer, boolean wasDeath) {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }

        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public Player getOriginal() {
            return original;
        }

        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public boolean isWasDeath() {
            return wasDeath;
        }
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     */
    public static class StartTracking extends PlayerEvent {
        public static final Event<IStartTracking> EVENT = ForgeEventFactory.create(IStartTracking.class, (listeners) -> (event) -> {
            for (IStartTracking listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IStartTracking {
            StartTracking onEvent(StartTracking event);
        }

        private final Entity target;

        public StartTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        /**
         * The Entity now being tracked.
         */
        public Entity getTarget() {
            return target;
        }
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     */
    public static class StopTracking extends PlayerEvent {
        public static final Event<IStopTracking> EVENT = ForgeEventFactory.create(IStopTracking.class, (listeners) -> (event) -> {
            for (IStopTracking listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IStopTracking {
            StopTracking onEvent(StopTracking event);
        }

        private final Entity target;

        public StopTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        /**
         * The Entity no longer being tracked.
         */
        public Entity getTarget() {
            return target;
        }
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    public static class LoadFromFile extends PlayerEvent {
        public static final Event<ILoadFromFile> EVENT = ForgeEventFactory.create(ILoadFromFile.class, (listeners) -> (event) -> {
            for (ILoadFromFile listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ILoadFromFile {
            LoadFromFile onEvent(LoadFromFile event);
        }

        private final File playerDirectory;
        private final String playerUUID;

        public LoadFromFile(Player player, File originDirectory, String playerUUID) {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         *
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory() {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID() {
            return playerUUID;
        }
    }

    /**
     * The player is being saved to the world store. Note that the
     * player may be in the process of logging out or otherwise departing
     * from the world. Don't assume it's association with the world.
     * This allows mods to load an additional file from the players directory
     * containing additional mod related player data.
     * <br>
     * Use this event to save the additional mod related player data to the world.
     *
     * <br>
     * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
     * corrupt the world state.
     */
    public static class SaveToFile extends PlayerEvent {
        public static final Event<ISaveToFile> EVENT = ForgeEventFactory.create(ISaveToFile.class, (listeners) -> (event) -> {
            for (ISaveToFile listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ISaveToFile {
            SaveToFile onEvent(SaveToFile event);
        }

        private final File playerDirectory;
        private final String playerUUID;

        public SaveToFile(Player player, File originDirectory, String playerUUID) {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         *
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory() {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID() {
            return playerUUID;
        }
    }

    public static class ItemPickupEvent extends PlayerEvent {
        public static final Event<IItemPickupEvent> EVENT = ForgeEventFactory.create(IItemPickupEvent.class, (listeners) -> (event) -> {
            for (IItemPickupEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IItemPickupEvent {
            ItemPickupEvent onEvent(ItemPickupEvent event);
        }

        /**
         * Original EntityItem with current remaining stack size
         */
        private final ItemEntity originalEntity;
        /**
         * Clone item stack, containing the item and amount picked up
         */
        private final ItemStack stack;

        public ItemPickupEvent(Player player, ItemEntity entPickedUp, ItemStack stack) {
            super(player);
            this.originalEntity = entPickedUp;
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemEntity getOriginalEntity() {
            return originalEntity;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        public static final Event<IItemCraftedEvent> EVENT = ForgeEventFactory.create(IItemCraftedEvent.class, (listeners) -> (event) -> {
            for (IItemCraftedEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IItemCraftedEvent {
            ItemCraftedEvent onEvent(ItemCraftedEvent event);
        }

        @NotNull
        private final ItemStack crafting;
        private final Container craftMatrix;

        public ItemCraftedEvent(Player player, @NotNull ItemStack crafting, Container craftMatrix) {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }

        @NotNull
        public ItemStack getCrafting() {
            return this.crafting;
        }

        public Container getInventory() {
            return this.craftMatrix;
        }
    }

    public static class ItemSmeltedEvent extends PlayerEvent {
        public static final Event<IItemSmeltedEvent> EVENT = ForgeEventFactory.create(IItemSmeltedEvent.class, (listeners) -> (event) -> {
            for (IItemSmeltedEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IItemSmeltedEvent {
            ItemSmeltedEvent onEvent(ItemSmeltedEvent event);
        }

        @NotNull
        private final ItemStack smelting;

        public ItemSmeltedEvent(Player player, @NotNull ItemStack crafting) {
            super(player);
            this.smelting = crafting;
        }

        @NotNull
        public ItemStack getSmelting() {
            return this.smelting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public static final Event<IPlayerLoggedInEvent> EVENT = ForgeEventFactory.create(IPlayerLoggedInEvent.class, (listeners) -> (event) -> {
            for (IPlayerLoggedInEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPlayerLoggedInEvent {
            PlayerLoggedInEvent onEvent(PlayerLoggedInEvent event);
        }

        public PlayerLoggedInEvent(Player player) {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public static final Event<IPlayerLoggedOutEvent> EVENT = ForgeEventFactory.create(IPlayerLoggedOutEvent.class, (listeners) -> (event) -> {
            for (IPlayerLoggedOutEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPlayerLoggedOutEvent {
            PlayerLoggedOutEvent onEvent(PlayerLoggedOutEvent event);
        }

        public PlayerLoggedOutEvent(Player player) {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        public static final Event<IPlayerRespawnEvent> EVENT = ForgeEventFactory.create(IPlayerRespawnEvent.class, (listeners) -> (event) -> {
            for (IPlayerRespawnEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPlayerRespawnEvent {
            PlayerRespawnEvent onEvent(PlayerRespawnEvent event);
        }

        private final boolean endConquered;

        public PlayerRespawnEvent(Player player, boolean endConquered) {
            super(player);
            this.endConquered = endConquered;
        }

        /**
         * Did this respawn event come from the player conquering the end?
         *
         * @return if this respawn was because the player conquered the end
         */
        public boolean isEndConquered() {
            return this.endConquered;
        }

    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        public static final Event<IPlayerChangedDimensionEvent> EVENT = ForgeEventFactory.create(IPlayerChangedDimensionEvent.class, (listeners) -> (event) -> {
            for (IPlayerChangedDimensionEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPlayerChangedDimensionEvent {
            PlayerChangedDimensionEvent onEvent(PlayerChangedDimensionEvent event);
        }

        private final ResourceKey<Level> fromDim;
        private final ResourceKey<Level> toDim;

        public PlayerChangedDimensionEvent(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }

        public ResourceKey<Level> getFrom() {
            return this.fromDim;
        }

        public ResourceKey<Level> getTo() {
            return this.toDim;
        }
    }

    /**
     * Fired when the game type of a server player is changed to a different value than what it was previously. Eg Creative to Survival, not Survival to Survival.
     * If the event is cancelled the game mode of the player is not changed and the value of <code>newGameMode</code> is ignored.
     */
    public static class PlayerChangeGameModeEvent extends PlayerEvent implements ICancellableEvent {
        public static final Event<IPlayerChangeGameModeEvent> EVENT = ForgeEventFactory.create(IPlayerChangeGameModeEvent.class, (listeners) -> (event) -> {
            for (IPlayerChangeGameModeEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IPlayerChangeGameModeEvent {
            PlayerChangeGameModeEvent onEvent(PlayerChangeGameModeEvent event);
        }

        private final GameType currentGameMode;
        private GameType newGameMode;

        public PlayerChangeGameModeEvent(Player player, GameType currentGameMode, GameType newGameMode) {
            super(player);
            this.currentGameMode = currentGameMode;
            this.newGameMode = newGameMode;
        }

        public GameType getCurrentGameMode() {
            return currentGameMode;
        }

        public GameType getNewGameMode() {
            return newGameMode;
        }

        /**
         * Sets the game mode the player will be changed to if this event is not cancelled.
         */
        public void setNewGameMode(GameType newGameMode) {
            this.newGameMode = newGameMode;
        }
    }
}
