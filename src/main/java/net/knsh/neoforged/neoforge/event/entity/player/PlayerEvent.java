package net.knsh.neoforged.neoforge.event.entity.player;

import net.knsh.neoforged.neoforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerEvent extends LivingEvent {
    private final Player player;

    public PlayerEvent(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }
}