package net.knsh.forgeeventapiport.mixin.world.level.block.state;

import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockBehaviorMixin {
    @Inject(
            method = "spawnAfterBreak",
            at = @At("HEAD")
    )
    private void neoforged$dropExpAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience, CallbackInfo ci) {
        if (dropExperience) CommonHooks.dropXpForBlock(state, level, pos, stack);
    }
}
