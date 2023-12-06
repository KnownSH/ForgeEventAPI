package net.knsh.neoforged;

import net.fabricmc.api.ClientModInitializer;

public class NeoForgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new testingClient();
    }
}
