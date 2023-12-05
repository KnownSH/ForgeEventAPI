package net.knsh.neoforged.fabric.accessors;

import net.minecraft.client.renderer.culling.Frustum;

public interface LevelRendererAccessor {
    Frustum getFrustum();

    int getTicks();
}
