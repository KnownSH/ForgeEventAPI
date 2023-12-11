package net.knsh.forgeeventapiport.accessors;

import net.minecraft.client.renderer.culling.Frustum;

public interface ForgeLevelRenderer {
    Frustum getFrustum();

    int getTicks();
}
