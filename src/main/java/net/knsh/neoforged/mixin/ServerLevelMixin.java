package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.accessors.EntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow public abstract void removePlayerImmediately(ServerPlayer player, Entity.RemovalReason reason);

    @Inject(
            method = "addPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z")
    )
    private void neoforged$onAddPlayer(ServerPlayer player, CallbackInfo ci) {
        ((EntityAccessor) player).onAddedToWorld();
    }

    @ModifyReturnValue(
            method = "addEntity",
            at = @At(value = "RETURN", ordinal = 1)
    )
    private boolean neoforged$onAddEntity(boolean original, @Local Entity entity) {
        if (original) {
            ((EntityAccessor) entity).onAddedToWorld();
            return true;
        } else {
            return false;
        }
    }
}
