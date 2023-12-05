package net.knsh.neoforged.accessors;

import net.minecraft.client.renderer.culling.Frustum;

public interface LevelRendererAccessor {
    Frustum getFrustum();

    int getTicks();
}
