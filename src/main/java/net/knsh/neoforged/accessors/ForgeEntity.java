package net.knsh.neoforged.accessors;

public interface ForgeEntity {
    boolean isAddedToWorld();

    void onAddedToWorld();

    void onRemovedFromWorld();
}
