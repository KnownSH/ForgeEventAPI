package net.knsh.neoforged.neoforge.event;

import net.knsh.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class EventHooks {
    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        event = PlayerEvent.HarvestCheck.EVENT.invoker().onHarvestCheck(event);
        return event.canHarvest();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
        if (LivingExperienceDropEvent.EVENT.invoker().onLivingExperienceDrop(event).isCanceled()) {
            return 0;
        }
        return event.getDroppedExperience();
    }
}
