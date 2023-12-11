package net.knsh.forgeeventapiport.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrowingPlantHeadBlock.class)
public class GrowingPlantHeadBlockMixin {
    @Shadow @Final private double growPerTickProbability;

    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextDouble()D")
    )
    private double neoforged$plantHeadReturnZero(RandomSource instance, Operation<Double> original) {
        return this.growPerTickProbability;
    }

    @WrapWithCondition(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean neoforged$plantHeadPreEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState) {
        return CommonHooks.onCropsGrowPre(instance, blockPos, blockState, instance.random.nextDouble() < this.growPerTickProbability);
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private void neoforged$plantHeadPostEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CommonHooks.onCropsGrowPost(level, pos, state);
    }
}
