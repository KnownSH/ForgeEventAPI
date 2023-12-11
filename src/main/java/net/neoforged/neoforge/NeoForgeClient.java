package net.neoforged.neoforge;

import net.fabricmc.api.ClientModInitializer;
import net.knsh.forgeeventapiport.testingClient;

public class NeoForgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new testingClient();
    }
}
