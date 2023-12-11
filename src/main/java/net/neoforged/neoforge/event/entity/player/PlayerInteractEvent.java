package net.neoforged.neoforge.event.entity.player;

import com.google.common.base.Preconditions;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public abstract class PlayerInteractEvent extends PlayerEvent {
    private final InteractionHand hand;
    private final BlockPos pos;
    @Nullable
    private final Direction face;
    private InteractionResult cancellationResult = InteractionResult.PASS;

    protected PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
        super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
        this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
        this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
        this.face = face;
    }

    public static class RightClickItem extends PlayerInteractEvent implements ICancellableEvent {
        public static final Event<RightClickItemEvent> EVENT = ForgeEventFactory.create(RightClickItemEvent.class, (listeners) -> (event) -> {
            for (RightClickItemEvent listener : listeners) {
                listener.onRightClickItem(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface RightClickItemEvent {
            RightClickItem onRightClickItem(RightClickItem event);
        }

        public RightClickItem(Player player, InteractionHand hand) {
            super(player, hand, player.blockPosition(), null);
        }
    }

    /**
     * @return The hand involved in this interaction. Will never be null.
     */
    @NotNull
    public InteractionHand getHand() {
        return hand;
    }

    /**
     * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
     */
    @NotNull
    public ItemStack getItemStack() {
        return getEntity().getItemInHand(hand);
    }

    /**
     * If the interaction was on an entity, will be a BlockPos centered on the entity.
     * If the interaction was on a block, will be the position of that block.
     * Otherwise, will be a BlockPos centered on the player.
     * Will never be null.
     *
     * @return The position involved in this interaction.
     */
    @NotNull
    public BlockPos getPos() {
        return pos;
    }

    /**
     * @return The face involved in this interaction. For all non-block interactions, this will return null.
     */
    @Nullable
    public Direction getFace() {
        return face;
    }

    /**
     * @return Convenience method to get the level of this interaction.
     */
    public Level getLevel() {
        return getEntity().level();
    }

    /**
     * @return The effective, i.e. logical, side of this interaction. This will be {@link EnvType#CLIENT} on the client thread, and {@link EnvType#SERVER} on the server thread.
     */
    public EnvType getSide() {
        return getLevel().isClientSide ? EnvType.CLIENT : EnvType.SERVER;
    }

    /**
     * @return The InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
     *         method of the event. By default, this is {@link InteractionResult#PASS}, meaning cancelled events will cause
     *         the client to keep trying more interactions until something works.
     */
    public InteractionResult getCancellationResult() {
        return cancellationResult;
    }

    /**
     * Set the InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
     * method of the event.
     * Note that this only has an effect on RightClickBlock}, {@link RightClickItem}, {EntityInteract}, and {EntityInteractSpecific}.
     */
    public void setCancellationResult(InteractionResult result) {
        this.cancellationResult = result;
    }
}
