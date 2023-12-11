package net.knsh.neoforged.mixin.world.entity.monster;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal")
public class EnderManMixin {
    @Shadow @Final private EnderMan enderman;

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan$EndermanLeaveBlockGoal;canPlaceBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean neoforged$endermanPlaceBlock(boolean original, @Local(ordinal = 1)BlockPos blockPos) {
        return original && !EventHooks.onBlockPlace(enderman, BlockSnapshot.create(this.enderman.level().dimension(), this.enderman.level(), blockPos), Direction.UP);
    }
}
