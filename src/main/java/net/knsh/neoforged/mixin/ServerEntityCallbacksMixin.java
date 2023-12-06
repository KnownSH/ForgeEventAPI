package net.knsh.neoforged.mixin;

import net.knsh.neoforged.accessors.EntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.EntityCallbacks.class)
public class ServerEntityCallbacksMixin {
    @Inject(
            method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;updateDynamicGameEventListener(Ljava/util/function/BiConsumer;)V")
    )
    private void neoforged$onTrackingStart(Entity entity, CallbackInfo ci) {
        ((EntityAccessor) entity).onRemovedFromWorld();
    }
}
