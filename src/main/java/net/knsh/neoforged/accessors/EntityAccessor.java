package net.knsh.neoforged.accessors;

public interface EntityAccessor {
    boolean isAddedToWorld();

    void onAddedToWorld();

    void onRemovedFromWorld();
}
