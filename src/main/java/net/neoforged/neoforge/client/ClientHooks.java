package net.neoforged.neoforge.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.knsh.forgeeventapiport.accessors.ForgeMinecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
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
        RenderLevelStageEvent.EVENT.invoker().onRenderLevelStageEvent(new RenderLevelStageEvent(stage, levelRenderer, poseStack, projectionMatrix, renderTick, ((ForgeMinecraft) mc).getPartialTick(), camera, frustum));
        profiler.pop();
    }

    public static CustomizeGuiOverlayEvent.BossEventProgress onCustomizeBossEventProgress(GuiGraphics guiGraphics, Window window, LerpingBossEvent bossInfo, int x, int y, int increment) {
        CustomizeGuiOverlayEvent.BossEventProgress evt = new CustomizeGuiOverlayEvent.BossEventProgress(window, guiGraphics,
                ((ForgeMinecraft)Minecraft.getInstance()).getPartialTick(), bossInfo, x, y, increment);
        evt = CustomizeGuiOverlayEvent.BossEventProgress.EVENT.invoker().onEvent(evt);
        return evt;
    }
}
