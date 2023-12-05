package net.knsh.neoforged.neoforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.knsh.neoforged.bus.api.SubscribeEvent;

public class testingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new testinginstance();
    }
}
