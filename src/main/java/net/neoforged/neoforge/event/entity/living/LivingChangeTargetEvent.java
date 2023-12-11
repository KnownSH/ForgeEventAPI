package net.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.fabric.ForgeEventFactory;
import net.neoforged.bus.fabric.SimpleEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

public class LivingChangeTargetEvent extends ForgeEvent implements ICancellableEvent {
    public static final Event<LivingChangeTarget> EVENT = ForgeEventFactory.create(LivingChangeTarget.class, (listeners) -> (event) -> {
        for (LivingChangeTarget listener : listeners) {
            listener.onLivingChangeTarget(event);
        }
        return event;
    });

    public static void onSubscription(Method method, Object object, ResourceLocation priority) {
        EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
    }

    @FunctionalInterface
    public interface LivingChangeTarget {
        LivingChangeTargetEvent onLivingChangeTarget(LivingChangeTargetEvent event);
    }

    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    private LivingEntity newTarget;
    private Entity entity;

    public LivingChangeTargetEvent(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType) {
        this.entity = entity;
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * {@return the new target of this entity.}
     */
    public LivingEntity getNewTarget() {
        return newTarget;
    }

    /**
     * Sets the new target this entity shall have.
     *
     * @param newTarget The new target of this entity.
     */
    public void setNewTarget(LivingEntity newTarget) {
        this.newTarget = newTarget;
    }

    /**
     * {@return the living target type.}
     */
    public ILivingTargetType getTargetType() {
        return targetType;
    }

    /**
     * {@return the original entity MC intended to use as a target before firing this event.}
     */
    public LivingEntity getOriginalTarget() {
        return originalTarget;
    }

    /**
     * A living target type indicates what kind of system caused a change of
     * targets. For a list of default target types, take a look at
     * {@link LivingTargetType}.
     */
    public static interface ILivingTargetType {

    }

    /**
     * This enum contains two default living target types.
     */
    public static enum LivingTargetType implements ILivingTargetType {
        /**
         * This target type indicates that the target has been set by calling {@link net.minecraft.world.entity.Mob#setTarget(LivingEntity)}.
         */
        MOB_TARGET,
        /**
         * This target type indicates that the target has been set by the {@link net.minecraft.world.entity.ai.behavior.StartAttacking} behavior.
         */
        BEHAVIOR_TARGET;
    }
}
