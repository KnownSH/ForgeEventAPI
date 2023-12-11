package net.knsh.forgeeventapiport.mixin.world.level.block;

import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"),
            cancellable = true
    )
    private void neoforged$cactusPreEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!CommonHooks.onCropsGrowPre(level, pos, state, true)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0)
    )
    private void neoforged$cactusPostEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CommonHooks.onCropsGrowPost(level, pos, state);
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1)
    )
    private void neoforged$cactusPostEvent1(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CommonHooks.onCropsGrowPost(level, pos, state);
    }
}
