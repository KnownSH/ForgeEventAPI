package net.knsh.forgeeventapiport.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ForgePlayer {
    float getDigSpeed(BlockState state, @Nullable BlockPos pos);
}
