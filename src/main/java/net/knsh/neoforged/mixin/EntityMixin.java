package net.knsh.neoforged.mixin;

import net.knsh.neoforged.accessors.EntityAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements EntityAccessor {
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
