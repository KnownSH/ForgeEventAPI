package net.knsh.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * This event is fired when an interaction between a {@link LivingEntity} and {@link MobEffectInstance} happens.
 * <p>
 * All children of this event are fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.
 */
public abstract class MobEffectEvent extends LivingEvent {
    @Nullable
    protected final MobEffectInstance effectInstance;

    public MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance) {
        super(living);
        this.effectInstance = effectInstance;
    }

    @Nullable
    public MobEffectInstance getEffectInstance() {
        return effectInstance;
    }

    /**
     * This Event is fired when a {@link net.minecraft.world.effect.MobEffect} is about to get removed from an Entity.
     * This Event is {@link net.knsh.neoforged.bus.api.ICancellableEvent}. If canceled, the effect will not be removed.
     * This Event does not have a result.
     */
    public static class Remove extends MobEffectEvent implements ICancellableEvent {
        public static final Event<IRemove> EVENT = ForgeEventFactory.create(IRemove.class, (listeners) -> (event) -> {
            for (IRemove listener : listeners) {
                listener.onRemoveEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IRemove {
            Remove onRemoveEvent(Remove event);
        }

        private final MobEffect effect;

        public Remove(LivingEntity living, MobEffect effect) {
            super(living, living.getEffect(effect));
            this.effect = effect;
        }

        public Remove(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
            this.effect = effectInstance.getEffect();
        }

        /**
         * @return the {@link MobEffectEvent} which is being removed from the entity
         */
        public MobEffect getEffect() {
            return this.effect;
        }

        /**
         * @return the {@link MobEffectInstance}. In the remove event, this can be null if the entity does not have a {@link MobEffect} of the right type active.
         */
        @Override
        @Nullable
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }

    /**
     * This event is fired to check if a {@link MobEffectInstance} can be applied to an entity.
     * This event is not {@link ICancellableEvent}.
     * This event {HasResult has a result}.
     * <p>
     * {@link Result#ALLOW ALLOW} will apply this mob effect.
     * {@link Result#DENY DENY} will not apply this mob effect.
     * {@link Result#DEFAULT DEFAULT} will run vanilla logic to determine if this mob effect is applicable in {@link LivingEntity#canBeAffected}.
     */
    public static class Applicable extends MobEffectEvent {
        public static final Event<IApplicable> EVENT = ForgeEventFactory.create(IApplicable.class, (listeners) -> (event) -> {
            for (IApplicable listener : listeners) {
                listener.onApplicableEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IApplicable {
            Applicable onApplicableEvent(Applicable event);
        }

        public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }

        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }
    }

    /**
     * This event is fired when a new {@link MobEffectInstance} is added to an entity.
     * This event is also fired if an entity already has the effect but with a different duration or amplifier.
     * This event is not {@link ICancellableEvent}.
     * This event does not have a result.
     */
    public static class Added extends MobEffectEvent {
        public static final Event<IAdded> EVENT = ForgeEventFactory.create(IAdded.class, (listeners) -> (event) -> {
            for (IAdded listener : listeners) {
                listener.onAddedEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IAdded {
            Added onAddedEvent(Added event);
        }

        private final MobEffectInstance oldEffectInstance;
        private final Entity source;

        public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
            super(living, newEffectInstance);
            this.oldEffectInstance = oldEffectInstance;
            this.source = source;
        }

        /**
         * @return the added {@link MobEffectInstance}. This is the unmerged MobEffectInstance if the old MobEffectInstance is not null.
         */
        @Override
        @NotNull
        public MobEffectInstance getEffectInstance() {
            return super.getEffectInstance();
        }

        /**
         * @return the old {@link MobEffectInstance}. This can be null if the entity did not have an effect of this kind before.
         */
        @Nullable
        public MobEffectInstance getOldEffectInstance() {
            return oldEffectInstance;
        }

        /**
         * @return the entity source of the effect, or {@code null} if none exists
         */
        @Nullable
        public Entity getEffectSource() {
            return source;
        }
    }

    /**
     * This event is fired when a {@link MobEffectInstance} expires on an entity.
     * This event is {@link ICancellableEvent}.
     * This event does not have a result.
     */
    public static class Expired extends MobEffectEvent implements ICancellableEvent {
        public static final Event<IExpired> EVENT = ForgeEventFactory.create(IExpired.class, (listeners) -> (event) -> {
            for (IExpired listener : listeners) {
                listener.onExpiredEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface IExpired {
            Expired onExpiredEvent(Expired event);
        }

        public Expired(LivingEntity living, MobEffectInstance effectInstance) {
            super(living, effectInstance);
        }
    }
}
