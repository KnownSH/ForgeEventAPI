package net.knsh.forgeeventapiport.mixin.world.item;

import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "useOn",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedPos()Lnet/minecraft/core/BlockPos;"),
            cancellable = true
    )
    private void neoforged$onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!context.getLevel().isClientSide) {
            cir.setReturnValue(CommonHooks.onPlaceItemIntoWorld(context));
        }
    }
}
