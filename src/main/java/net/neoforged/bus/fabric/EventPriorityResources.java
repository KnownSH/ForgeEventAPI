package net.neoforged.bus.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.NeoForge;

public class EventPriorityResources {
    public static final ResourceLocation HIGHEST = new ResourceLocation(NeoForge.MOD_ID, "highest");
    public static final ResourceLocation HIGH = new ResourceLocation(NeoForge.MOD_ID, "high");
    public static final ResourceLocation NORMAL = Event.DEFAULT_PHASE;
    public static final ResourceLocation LOW = new ResourceLocation(NeoForge.MOD_ID, "low");
    public static final ResourceLocation LOWEST = new ResourceLocation(NeoForge.MOD_ID, "lowest");
}
