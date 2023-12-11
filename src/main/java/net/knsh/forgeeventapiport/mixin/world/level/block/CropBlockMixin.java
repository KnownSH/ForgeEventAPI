package net.knsh.forgeeventapiport.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public class CropBlockMixin {
    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    )
    private int neoforged$cropReturnZero(RandomSource instance, int i, Operation<Integer> original) {
        return 0;
    }

    @WrapWithCondition(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private boolean neoforged$cropPreEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CommonHooks.onCropsGrowPre(instance, blockPos, blockState, instance.random.nextInt(5) == 0);
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private void neoforged$cropPostEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CommonHooks.onCropsGrowPost(level, pos, state);
    }
}
