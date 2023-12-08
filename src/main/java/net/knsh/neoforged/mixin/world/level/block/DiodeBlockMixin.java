package net.knsh.neoforged.mixin.world.level.block;

import net.knsh.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin extends HorizontalDirectionalBlock {
    protected DiodeBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "updateNeighborsInFront",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V"),
            cancellable = true
    )
    private void neoforged$onDiodeNeighborChanged(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (EventHooks.onNeighborNotify(level, pos, state, EnumSet.of(state.getValue(FACING).getOpposite()), false).isCanceled()) {
            ci.cancel();
        }
    }
}
