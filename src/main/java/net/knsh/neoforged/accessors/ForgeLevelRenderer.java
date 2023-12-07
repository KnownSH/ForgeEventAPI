package net.knsh.neoforged.accessors;

import net.minecraft.client.renderer.culling.Frustum;

public interface ForgeLevelRenderer {
    Frustum getFrustum();

    int getTicks();
}
