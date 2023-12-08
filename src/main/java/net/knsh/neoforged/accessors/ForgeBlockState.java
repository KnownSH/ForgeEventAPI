package net.knsh.neoforged.accessors;

import net.knsh.neoforged.neoforge.common.ToolAction;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import net.knsh.neoforged.neoforge.common.ToolActions;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ForgeBlockState {
    int getExpDrop(LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel);
}
