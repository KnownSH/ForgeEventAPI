package net.knsh.neoforged.neoforge.event.level;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ExplosionEvent triggers when an explosion happens in the level.<br>
 * <br>
 * ExplosionEvent.Start is fired before the explosion actually occurs.<br>
 * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities.<br>
 * <br>
 * ExplosionEvent.Start is {@link ICancellableEvent}.<br>
 * ExplosionEvent.Detonate can modify the affected blocks and entities.<br>
 * Children do not use {HasResult}.<br>
 * Children of this event are fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.<br>
 */
public abstract class ExplosionEvent extends ForgeEvent {
    private final Level level;
    private final Explosion explosion;

    public ExplosionEvent(Level level, Explosion explosion) {
        this.level = level;
        this.explosion = explosion;
    }

    public Level getLevel() {
        return level;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    /**
     * ExplosionEvent.Start is fired before the explosion actually occurs. Canceling this event will stop the explosion.<br>
     * <br>
     * This event is {@link net.knsh.neoforged.bus.api.ICancellableEvent}.<br>
     * This event does not use {HasResult}.<br>
     * This event is fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.<br>
     */
    public static class Start extends ExplosionEvent implements ICancellableEvent {
        public static final Event<IStart> EVENT = ForgeEventFactory.create(IStart.class, (listeners) -> (event) -> {
            for (IStart listener : listeners) {
                event = listener.onStartEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IStart {
            Start onStartEvent(Start event);
        }

        public Start(Level level, Explosion explosion) {
            super(level, explosion);
        }
    }

    /**
     * ExplosionEvent.Detonate is fired once the explosion has a list of affected blocks and entities. These lists can be modified to change the outcome.<br>
     * <br>
     * This event is not {@link ICancellableEvent}.<br>
     * This event does not use {HasResult}.<br>
     * This event is fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.<br>
     */
    public static class Detonate extends ExplosionEvent {
        public static final Event<IDetonate> EVENT = ForgeEventFactory.create(IDetonate.class, (listeners) -> (event) -> {
            for (IDetonate listener : listeners) {
                event = listener.onDetonateEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IDetonate {
            Detonate onDetonateEvent(Detonate event);
        }

        private final List<Entity> entityList;

        public Detonate(Level level, Explosion explosion, List<Entity> entityList) {
            super(level, explosion);
            this.entityList = entityList;
        }

        /** return the list of blocks affected by the explosion. */
        public List<BlockPos> getAffectedBlocks() {
            return getExplosion().getToBlow();
        }

        /** return the list of entities affected by the explosion. */
        public List<Entity> getAffectedEntities() {
            return entityList;
        }
    }
}
