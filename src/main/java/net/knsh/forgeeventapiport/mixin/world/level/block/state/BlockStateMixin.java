package net.knsh.forgeeventapiport.mixin.world.level.block.state;

import net.knsh.forgeeventapiport.accessors.ForgeBlock;
import net.knsh.forgeeventapiport.accessors.ForgeBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements ForgeBlockState {
    @Override
    public int getExpDrop(LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return ((ForgeBlock)((BlockState) (Object) this).getBlock()).getExpDrop((BlockState) (Object) this, level, randomSource, pos, fortuneLevel, silkTouchLevel);
    }
}
