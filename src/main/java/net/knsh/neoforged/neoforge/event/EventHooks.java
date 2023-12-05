package net.knsh.neoforged.neoforge.event;

import net.knsh.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class EventHooks {
    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        event = PlayerEvent.HarvestCheck.EVENT.invoker().onHarvestCheck(event);
        return event.canHarvest();
    }
}
