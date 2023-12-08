package net.knsh.neoforged.mixin.world.level.material;

import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin {
    @Shadow protected abstract boolean isFlammable(LevelReader level, BlockPos pos);

    @ModifyArg(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0),
            index = 1
    )
    private BlockState neoforged$randomTick(BlockState state, @Local Level level, @Local(ordinal = 1) BlockPos pos, @Local(ordinal = 0) BlockPos liquidPos) {
        return EventHooks.fireFluidPlaceBlockEvent(level, pos, liquidPos, Blocks.FIRE.defaultBlockState());
    }

    @ModifyArg(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            index = 1
    )
    private BlockState neoforged$randomTick2(BlockState state, @Local Level level, @Local(ordinal = 1) BlockPos pos, @Local(ordinal = 0) BlockPos liquidPos) {
        return EventHooks.fireFluidPlaceBlockEvent(level, pos.above(), liquidPos, Blocks.FIRE.defaultBlockState());
    }
}
