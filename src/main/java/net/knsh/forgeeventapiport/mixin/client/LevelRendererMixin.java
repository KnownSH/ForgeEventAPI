package net.knsh.forgeeventapiport.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.knsh.forgeeventapiport.accessors.ForgeLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements ForgeLevelRenderer {
    @Shadow private int ticks;
    @Shadow private @Nullable Frustum capturedFrustum;
    @Shadow private Frustum cullingFrustum;
    @Shadow @Final private Minecraft minecraft;

    @Unique
    public Frustum getFrustum() {
        return this.capturedFrustum != null ? this.capturedFrustum : this.cullingFrustum;
    }

    @Unique
    public int getTicks() {
        return this.ticks;
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 6)
    )
    private void neoforged$afterSkyEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_SKY, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "CONSTANT", args = "stringValue=blockentities")
    )
    private void neoforged$afterEntitiesEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_ENTITIES, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "CONSTANT", args = "stringValue=destroyProgress")
    )
    private void neoforged$afterBlockEntitiesEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V")
    )
    private void neoforged$afterParticlesEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", ordinal = 1)
    )
    private void neoforged$afterOtherParticlesEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V")
    )
    private void neoforged$afterWeatherEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_WEATHER, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderWorldBorder(Lnet/minecraft/client/Camera;)V", ordinal = 1)
    )
    private void neoforged$afterOtherWeatherEvent(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci, @Local LocalRef<Frustum> frustum) {
        ClientHooks.dispatchRenderStage(RenderLevelStageEvent.Stage.AFTER_WEATHER, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, camera, frustum.get());
    }

    @Inject(
            method = "renderChunkLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;clearRenderState()V")
    )
    private void neoforged$afterChunkEvent(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
        ClientHooks.dispatchRenderStage(renderType, (LevelRenderer) (Object) this, poseStack, projectionMatrix, this.ticks, this.minecraft.gameRenderer.getMainCamera(), this.getFrustum());
    }
}
