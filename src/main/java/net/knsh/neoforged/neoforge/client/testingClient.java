package net.knsh.neoforged.neoforge.client;

import net.fabricmc.api.ClientModInitializer;

public class testingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new testinginstance();
    }
}
