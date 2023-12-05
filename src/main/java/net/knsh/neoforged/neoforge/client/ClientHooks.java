package net.knsh.neoforged.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.knsh.neoforged.NeoForge;
import net.knsh.neoforged.accessors.MinecraftAccessor;
import net.knsh.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ClientHooks {
    public static void dispatchRenderStage(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        RenderLevelStageEvent.Stage stage = RenderLevelStageEvent.Stage.fromRenderType(renderType);
        if (stage != null)
            dispatchRenderStage(stage, levelRenderer, poseStack, projectionMatrix, renderTick, camera, frustum);
    }

    public static void dispatchRenderStage(RenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        Minecraft mc = Minecraft.getInstance();
        ProfilerFiller profiler = mc.getProfiler();
        profiler.push(stage.toString());
        RenderLevelStageEvent.EVENT.invoker().onRenderLevelStageEvent(new RenderLevelStageEvent(stage, levelRenderer, poseStack, projectionMatrix, renderTick, ((MinecraftAccessor) mc).getPartialTick(), camera, frustum));
        profiler.pop();
    }
}
