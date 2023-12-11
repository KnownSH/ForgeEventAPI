package net.knsh.neoforged.mixin.world.entity;

import net.knsh.neoforged.accessors.ForgeEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements ForgeEntity {
    @Unique private boolean isAddedToWorld;

    public EntityMixin(boolean isAddedToWorld) {
        this.isAddedToWorld = isAddedToWorld;
    }

    @Unique
    public final boolean isAddedToWorld() {
        return this.isAddedToWorld;
    }

    @Unique
    public void onAddedToWorld() {
        this.isAddedToWorld = true;
    }

    @Unique
    public void onRemovedFromWorld() {
        this.isAddedToWorld = false;
    }
}
