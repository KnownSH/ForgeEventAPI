package net.knsh.forgeeventapiport.mixin.world.level.entity;

import net.knsh.forgeeventapiport.accessors.ForgeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {
    @Inject(
            method = "method_31864",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z")
    )
    private void neoforged$onAddEntity(EntityAccess entity, CallbackInfo ci) {
        if (entity instanceof Entity entity1) {
            ((ForgeEntity) entity1).onAddedToWorld();
        }
    }

    @Inject(
            method = "method_31863",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z")
    )
    private void neoforged$onAddEntity2(EntityAccess entity, CallbackInfo ci) {
        if (entity instanceof Entity entity1) {
            ((ForgeEntity) entity1).onAddedToWorld();
        }
    }

    @Inject(
            method = "method_31857",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z")
    )
    private void neoforged$onAddEntity3(EntityAccess entity, CallbackInfo ci) {
        if (entity instanceof Entity entity1) {
            ((ForgeEntity) entity1).onAddedToWorld();
        }
    }
}
