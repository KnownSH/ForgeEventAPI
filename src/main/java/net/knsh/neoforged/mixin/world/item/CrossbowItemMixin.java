package net.knsh.neoforged.mixin.world.item;

import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(
            method = "performShooting",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void neoforged$onPerformShootingEvent(Level level, LivingEntity shooter, InteractionHand usedHand, ItemStack stack, float velocity, float inaccuracy, CallbackInfo ci) {
        if (shooter instanceof Player player && EventHooks.onArrowLoose(stack, shooter.level(), player, 1, true) < 0) ci.cancel();
    }
}
