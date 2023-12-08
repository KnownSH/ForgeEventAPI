package net.knsh.neoforged.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {
    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    )
    private int neoforged$sweetBerryBushReturnZero(RandomSource instance, int i, Operation<Integer> original) {
        return 0;
    }

    @WrapWithCondition(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private boolean neoforged$sweetBerryBushPreEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CommonHooks.onCropsGrowPre(instance, blockPos, blockState, instance.random.nextInt(5) == 0);
    }

    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private boolean neoforged$sweetBerryBushPostEvent(ServerLevel level, BlockPos pos, BlockState state, int flags, Operation<Void> original) {
        original.call(level, pos, state, flags);
        CommonHooks.onCropsGrowPost(level, pos, state);
        return true;
    }
}
