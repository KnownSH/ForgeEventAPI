package net.knsh.forgeeventapiport.mixin.world.level.block;

import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature") // mixin actin silly again
    @ModifyVariable(
            method = "onPlace",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/portal/PortalShape;findEmptyPortalShape(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)Ljava/util/Optional;")
    )
    private Optional<PortalShape> neoforged$onPlace(Optional<PortalShape> value, BlockState state, Level level, BlockPos pos) {
        return EventHooks.onTrySpawnPortal(level, pos, value);
    }
}
