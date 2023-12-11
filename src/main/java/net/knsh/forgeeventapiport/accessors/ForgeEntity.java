package net.knsh.forgeeventapiport.accessors;

public interface ForgeEntity {
    boolean isAddedToWorld();

    void onAddedToWorld();

    void onRemovedFromWorld();
}
