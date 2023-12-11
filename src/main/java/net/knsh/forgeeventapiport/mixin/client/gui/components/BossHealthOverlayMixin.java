package net.knsh.forgeeventapiport.mixin.client.gui.components;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V")
    )
    private void neoforged$onRenderBossBar(BossHealthOverlay instance, GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, Operation<Void> original, @Share("event") LocalRef<CustomizeGuiOverlayEvent.BossEventProgress> eventShared, @Local LerpingBossEvent lerpingBossEvent) {
        CustomizeGuiOverlayEvent.BossEventProgress event = ClientHooks.onCustomizeBossEventProgress(guiGraphics, minecraft.getWindow(), lerpingBossEvent, x, y, 0);
        eventShared.set(event);
        if (!event.isCanceled()) {
            original.call(instance, guiGraphics, x, y, lerpingBossEvent);
        }
    }

    @WrapWithCondition(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I")
    )
    private boolean neoforged$onRenderBossBarText(GuiGraphics instance, Font font, Component text, int x, int y, int color, @Share("event")LocalRef<CustomizeGuiOverlayEvent.BossEventProgress> eventShared) {
        return !eventShared.get().isCanceled();
    }

    @ModifyConstant(
            method = "render",
            constant = @Constant(intValue = 10)
    )
    private int neoforged$modifyBossBarBasedUponEvent(int original, @Share("event")LocalRef<CustomizeGuiOverlayEvent.BossEventProgress> eventShared) {
        if (!eventShared.get().isCanceled()) {
            return eventShared.get().getIncrement();
        }
        return 0;
    }

    @ModifyConstant(
            method = "render",
            constant = @Constant(intValue = 9, ordinal = 1)
    )
    private int neoforged$disableAdd9ForBarLengthEvent(int original) {
        return 0;
    }
}
