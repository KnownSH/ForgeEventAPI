package net.knsh.forgeeventapiport;

import net.neoforged.neoforge.NeoForge;

public class testingClient {
    public testingClient() {
        NeoForge.EVENT_BUS.register(this);
    }
}
