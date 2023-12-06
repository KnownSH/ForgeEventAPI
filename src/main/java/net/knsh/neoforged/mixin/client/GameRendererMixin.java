package net.knsh.neoforged.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.knsh.neoforged.accessors.LevelRendererAccessor;
import net.knsh.neoforged.neoforge.client.ClientHooks;
import net.knsh.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1)
    )
    private void forgeevents$onRenderLevelEvent(
            float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci,
            @Local LocalRef<Matrix4f> matrix4f, @Local LocalRef<Camera> camera)
    {
        ClientHooks.dispatchRenderStage(
                RenderLevelStageEvent.Stage.AFTER_LEVEL,
                this.minecraft.levelRenderer,
                poseStack,
                matrix4f.get(),
                ((LevelRendererAccessor) this.minecraft.levelRenderer).getTicks(),
                camera.get(),
                ((LevelRendererAccessor) this.minecraft.levelRenderer).getFrustum()
        );
    }
}
