/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.neoforged.bus;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ForgeEvent;
import net.neoforged.bus.api.IEventClassChecker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.fabric.EventPriorityResources;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

public class EventSubscriptionHandler {

    public EventSubscriptionHandler() {
    }

    public void register(final Object target) {
        boolean isStatic = target.getClass() == Class.class;
        Class<?> clazz = isStatic ? (Class<?>) target : target.getClass();

        checkSupertypes(clazz, clazz);

        int foundMethods = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers()) == isStatic) {
                registerListener(target, method, method);
            } else {
                if (isStatic) {
                    throw new IllegalArgumentException("""
                            Expected @SubscribeEvent method %s to be static
                            because register() was called with a class type.
                            Either make the method static, or call register() with an instance of %s.
                            """.formatted(method, clazz));
                } else {
                    throw new IllegalArgumentException("""
                            Expected @SubscribeEvent method %s to NOT be static
                            because register() was called with an instance type.
                            Either make the method non-static, or call register(%s.class).
                            """.formatted(method, clazz.getSimpleName()));
                }
            }

            ++foundMethods;
        }
    }

    // Refabricated by KnownSH, uses fabric events
    private void registerListener(final Object target, final Method method, final Method real) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1)
        {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation. " +
                            "It has " + parameterTypes.length + " arguments, " +
                            "but event handler methods require a single argument only."
            );
        }

        Class<?> eventType = parameterTypes[0];

        try {
            IEventClassChecker.check((Class<? extends ForgeEvent>) eventType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation, " +
                            "but takes an argument that is not valid for this bus" + eventType, e);
        }

        register(eventType, target, real);
    }

    private void register(Class<?> eventType, Object target, Method method) {
        try {
            EventPriority priority = method.getAnnotation(SubscribeEvent.class).priority();
            ResourceLocation priorityIdentifier = switch (priority) {
                case HIGHEST -> EventPriorityResources.HIGHEST;
                case HIGH -> EventPriorityResources.HIGH;
                case NORMAL -> EventPriorityResources.NORMAL;
                case LOW -> EventPriorityResources.LOW;
                case LOWEST -> EventPriorityResources.LOWEST;
            };

            eventType.getMethod("onSubscription", Method.class, Object.class, ResourceLocation.class).invoke(null, method, target, priorityIdentifier);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkSupertypes(Class<?> registeredType, Class<?> type) {
        if (type == null || type == Object.class) {
            return;
        }

        if (type != registeredType) {
            for (var method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SubscribeEvent.class)) {
                    throw new IllegalArgumentException("""
                            Attempting to register a listener object of type %s,
                            however its supertype %s has a @SubscribeEvent method: %s.
                            This is not allowed! Only the listener object can have @SubscribeEvent methods.
                            """.formatted(registeredType, type, method));
                }
            }
        }

        checkSupertypes(registeredType, type.getSuperclass());
        Stream.of(type.getInterfaces())
                .forEach(itf -> checkSupertypes(registeredType, itf));
    }
}
