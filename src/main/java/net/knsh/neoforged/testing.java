package net.knsh.neoforged;

import net.knsh.neoforged.bus.api.SubscribeEvent;
import net.knsh.neoforged.neoforge.event.level.BlockEvent;

public class testing {
    public testing() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void testEvent(BlockEvent.BreakEvent event) {
        NeoForge.LOGGER.info("Block broken at " + event.getPos().toString());
    }
}
