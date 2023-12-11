package net.knsh.forgeeventapiport.mixin.client.gui.components;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderLines(GuiGraphics guiGraphics, List<String> lines, boolean leftSide);

    @Inject(
            method = "method_51746",
            at = @At("HEAD")
    )
    private void neoforged$onRenderDebugScreen(GuiGraphics graphics, CallbackInfo ci, @Share("gameInfo")LocalRef<List<String>> gameInfo, @Share("systemInfo")LocalRef<List<String>> systemInfo) {
        final List<String> gameInformation = new ArrayList<>();
        final List<String> systemInformation = new ArrayList<>();
        CustomizeGuiOverlayEvent.DebugText event = new CustomizeGuiOverlayEvent.DebugText(minecraft.getWindow(), graphics, minecraft.getFrameTime(), gameInformation, systemInformation);
        event = CustomizeGuiOverlayEvent.DebugText.EVENT.invoker().onEvent(event);
        gameInfo.set(event.getLeft());
        systemInfo.set(event.getRight());
    }

    @Inject(
            method = "drawGameInformation",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2)
    )
    private void neoforged$onRenderGameInformation(GuiGraphics guiGraphics, CallbackInfo ci, @Local List<String> list, @Share("gameInfo")LocalRef<List<String>> gameInfo) {
        if (gameInfo.get() != null) list.addAll(gameInfo.get());
    }

    @Inject(
            method = "drawSystemInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V")
    )
    private void neoforged$onRenderSystemInformation(GuiGraphics guiGraphics, CallbackInfo ci, @Local List<String> list, @Share("systemInfo")LocalRef<List<String>> gameInfo) {
        if (gameInfo.get() != null) list.addAll(gameInfo.get());
    }
}
