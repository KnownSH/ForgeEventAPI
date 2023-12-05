package net.knsh.neoforged.neoforge.client;

import net.knsh.neoforged.NeoForge;
import net.knsh.neoforged.bus.api.SubscribeEvent;
import net.knsh.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class testinginstance {
    public testinginstance() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRender(RenderLevelStageEvent event) {
        NeoForge.LOGGER.info("RenderLevelStageEvent fired" + event.getStage().toString());
    }
}
