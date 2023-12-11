package net.knsh.forgeeventapiport.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BambooStalkBlock.class)
public class BambooStalkBlockMixin {
    @WrapWithCondition(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BambooStalkBlock;growBamboo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;I)V")
    )
    private boolean neoforged$bambooPreEvent(BambooStalkBlock instance, BlockState state, Level level, BlockPos pos, RandomSource random, int age) {
        return CommonHooks.onCropsGrowPre(level, pos, state, true);
    }

    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BambooStalkBlock;growBamboo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;I)V")
    )
    private void neoforged$afterBambooGrowthEvent(BambooStalkBlock instance, BlockState state, Level level, BlockPos pos, RandomSource random, int age, Operation<Void> original) {
        original.call(instance, state, level, pos, random, age);
        CommonHooks.onCropsGrowPost(level, pos, state);
    }
}
