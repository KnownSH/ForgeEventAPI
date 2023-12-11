/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.knsh.neoforged.neoforge.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.knsh.neoforged.bus.api.ICancellableEvent;
import net.knsh.neoforged.bus.fabric.ForgeEventFactory;
import net.knsh.neoforged.bus.fabric.SimpleEventHandler;
import net.knsh.neoforged.neoforge.event.entity.EntityEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class LivingEvent extends EntityEvent {
    private final LivingEntity livingEntity;

    public LivingEvent(LivingEntity entity) {
        super(entity);
        livingEntity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }

    public static class LivingTickEvent extends LivingEvent implements ICancellableEvent {
        public static final Event<ILivingTickEvent> EVENT = ForgeEventFactory.create(ILivingTickEvent.class, (listeners) -> (event) -> {
            for (ILivingTickEvent listener : listeners) {
                event = listener.onLivingTickEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ILivingTickEvent {
            LivingTickEvent onLivingTickEvent(LivingTickEvent event);
        }

        public LivingTickEvent(LivingEntity e) {
            super(e);
        }
    }

    /**
     * LivingJumpEvent is fired when an Entity jumps.<br>
     * This event is fired whenever an Entity jumps in
     * {@code LivingEntity#jumpFromGround()}, {@code MagmaCube#jumpFromGround()},
     * and {@code Horse#jumpFromGround()}.<br>
     * <br>
     * This event is fired via the {@link net.knsh.neoforged.neoforge.common.CommonHooks#onLivingJump(LivingEntity)}.<br>
     * <br>
     * This event is not {@link ICancellableEvent}.<br>
     * <br>
     * This event does not have a result. {HasResult}<br>
     * <br>
     * This event is fired on the {@link net.knsh.neoforged.NeoForge#EVENT_BUS}.
     **/
    public static class LivingJumpEvent extends LivingEvent {
        public static final Event<ILivingJumpEvent> EVENT = ForgeEventFactory.create(ILivingJumpEvent.class, (listeners) -> (event) -> {
            for (ILivingJumpEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ILivingJumpEvent {
            LivingJumpEvent onEvent(LivingJumpEvent event);
        }

        public LivingJumpEvent(LivingEntity e) {
            super(e);
        }
    }

    public static class LivingVisibilityEvent extends LivingEvent {
        public static final Event<ILivingVisibilityEvent> EVENT = ForgeEventFactory.create(ILivingVisibilityEvent.class, (listeners) -> (event) -> {
            for (ILivingVisibilityEvent listener : listeners) {
                event = listener.onEvent(event);
            }
            return event;
        });

        public static void onSubscription(Method method, Object object, ResourceLocation priority) {
            EVENT.register(priority, (event) -> SimpleEventHandler.create(method, object, priority, event));
        }

        @FunctionalInterface
        public interface ILivingVisibilityEvent {
            LivingVisibilityEvent onEvent(LivingVisibilityEvent event);
        }

        private double visibilityModifier;
        @Nullable
        private final Entity lookingEntity;

        public LivingVisibilityEvent(LivingEntity livingEntity, @Nullable Entity lookingEntity, double originalMultiplier) {
            super(livingEntity);
            this.visibilityModifier = originalMultiplier;
            this.lookingEntity = lookingEntity;
        }

        /**
         * @param mod Is multiplied with the current modifier
         */
        public void modifyVisibility(double mod) {
            visibilityModifier *= mod;
        }

        /**
         * @return The current modifier
         */
        public double getVisibilityModifier() {
            return visibilityModifier;
        }

        /**
         * @return The entity trying to see this LivingEntity, if available
         */
        @Nullable
        public Entity getLookingEntity() {
            return lookingEntity;
        }
    }
}
