package net.knsh.neoforged.neoforge.client;

import net.knsh.neoforged.NeoForge;
import net.knsh.neoforged.bus.api.SubscribeEvent;
import net.knsh.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class testinginstance {
    public testinginstance() {
        NeoForge.EVENT_BUS.register(this);
    }
}
