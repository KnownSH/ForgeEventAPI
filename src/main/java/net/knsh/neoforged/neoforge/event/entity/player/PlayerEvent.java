package net.knsh.neoforged.neoforge.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.knsh.neoforged.neoforge.event.entity.living.LivingEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;

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
}
