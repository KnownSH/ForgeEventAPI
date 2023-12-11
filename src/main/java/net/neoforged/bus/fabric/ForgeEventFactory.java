package net.neoforged.bus.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Function;

public class ForgeEventFactory {
    public static <T> Event<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return EventFactory.createWithPhases(type, invokerFactory, EventPriorityResources.HIGHEST, EventPriorityResources.HIGH, EventPriorityResources.NORMAL, EventPriorityResources.LOW, EventPriorityResources.LOWEST);
    }
}
