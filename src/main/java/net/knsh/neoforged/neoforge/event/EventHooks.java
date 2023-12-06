package net.knsh.neoforged.neoforge.event;

import net.knsh.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.knsh.neoforged.neoforge.event.level.ExplosionEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return ExplosionEvent.Start.EVENT.invoker().onStartEvent(new ExplosionEvent.Start(level, explosion)).isCanceled();
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        ExplosionEvent.Detonate.EVENT.invoker().onDetonateEvent(new ExplosionEvent.Detonate(level, explosion, list));
    }
}
