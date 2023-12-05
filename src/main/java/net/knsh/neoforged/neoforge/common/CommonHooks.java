package net.knsh.neoforged.neoforge.common;

import net.knsh.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CommonHooks {
    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        event = LivingChangeTargetEvent.EVENT.invoker().onLivingChangeTarget(event);
        return event;
    }

    public static LivingDamageEvent onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        event = LivingDamageEvent.EVENT.invoker().onLivingDamage(event);
        return event;
    }

    public static Boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        LivingDeathEvent event = new LivingDeathEvent(entity, src);
        return LivingDeathEvent.EVENT.invoker().onLivingDeath(event).isCanceled();
    }

    public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        evt = PlayerInteractEvent.RightClickItem.EVENT.invoker().onRightClickItem(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }
}
