package net.knsh.neoforged.neoforge.event.level;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;

public abstract class BlockEvent extends ForgeEvent {
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

        public BreakEvent(Level level, BlockPos pos, BlockState state, Player player) {
            super(level, pos, state);
            this.player = player;

            if (state == null || !CommonHooks.isCorrectToolForDrops(state, player)) // Handle empty block or player unable to break block scenario
            {
                //this.exp = 0;
            } else {
                //int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
                //int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
            }
        }

        public Player getPlayer() {
            return player;
        }
    }
}
