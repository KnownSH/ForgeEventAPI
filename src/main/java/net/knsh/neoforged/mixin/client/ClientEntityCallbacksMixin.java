package net.knsh.neoforged.mixin.client;

import net.knsh.neoforged.accessors.ForgeEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$EntityCallbacks")
public class ClientEntityCallbacksMixin {
    @Inject(
            method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z")
    )
    private void neoforged$onRemoveEntityFromWorld(Entity entity, CallbackInfo ci) {
        ((ForgeEntity) entity).onRemovedFromWorld();
    }
}
