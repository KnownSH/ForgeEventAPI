package net.knsh.neoforged.mixin.client;

import net.knsh.neoforged.accessors.EntityAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.EntityCallbacks.class)
public class ClientEntityCallbacksMixin {
    @Inject(
            method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z")
    )
    private void neoforged$onRemoveEntityFromWorld(Entity entity, CallbackInfo ci) {
        ((EntityAccessor) entity).onRemovedFromWorld();
    }
}
