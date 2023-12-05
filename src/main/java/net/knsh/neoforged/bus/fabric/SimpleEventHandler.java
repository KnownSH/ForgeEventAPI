package net.knsh.neoforged.bus.fabric;

import net.knsh.neoforged.bus.api.ForgeEvent;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;

public class SimpleEventHandler {
    public static <T extends ForgeEvent> T create(Method method, Object object, ResourceLocation priority, T event) {
        try {
            method.invoke(object, event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return event;
    }
}
