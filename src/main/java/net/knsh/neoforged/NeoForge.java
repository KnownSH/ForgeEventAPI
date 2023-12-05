package net.knsh.neoforged;

import net.fabricmc.api.ModInitializer;

import net.knsh.neoforged.bus.EventSubscriptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoForge implements ModInitializer {
	public static final String MOD_ID = "neoforge";
    public static final Logger LOGGER = LoggerFactory.getLogger("neoforge");
	public static final EventSubscriptionHandler EVENT_BUS = new EventSubscriptionHandler();

	@Override
	public void onInitialize() {
		//placeholder
	}
}