package net.knsh.neoforged.mixin.server.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.knsh.neoforged.neoforge.common.CommonHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;

    @Inject(
            method = "useItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"),
            cancellable = true
    )
    private void neoforged$useItemEvent(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult cancelResult = CommonHooks.onItemRightClick(player, hand);
        if (cancelResult != null) {
            cir.setReturnValue(cancelResult);
        }
    }

    @WrapOperation(
            method = "destroyBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canAttackBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean neoforged$onBlockBreakEvent(Item instance, BlockState state, Level level, BlockPos pos, Player player, Operation<Boolean> original, @Share("exp")LocalIntRef expShare) {
        int exp = CommonHooks.onBlockBreakEvent(level, player.getServer().getDefaultGameType(), (ServerPlayer) player, pos);
        expShare.set(exp);
        return !(exp == -1); // return inverse because it should be inverted again in the original method
    }

    // currently broken
    /*
    @Inject(
            method = "destroyBlock",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V")
    )
    private void neoforged$dropExpOnBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Share("exp")LocalIntRef expShare, @Local boolean flag) {
        if (flag && expShare.get() > 0) {
            this.level.getBlockState(pos).getBlock().popExperience(level, pos, expShare.get());
        }
    }
     */
}
