package net.knsh.neoforged.mixin.world.item.enchantment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @ModifyExpressionValue(
            method = "onEntityMoved",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z")
    )
    private static boolean neoforged$frostWalkerBlockPlace(boolean value, LivingEntity living, Level level, BlockPos pos) {
        return value && !EventHooks.onBlockPlace(living, BlockSnapshot.create(level.dimension(), level, pos), Direction.UP);
    }
}
