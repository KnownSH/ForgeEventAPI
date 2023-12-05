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
import net.minecraft.world.entity.LivingEntity;

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
}
