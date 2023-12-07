package net.knsh.neoforged.mixin.client;

import net.knsh.neoforged.accessors.ForgeMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements ForgeMinecraft {
    @Shadow private volatile boolean pause;
    @Shadow private float pausePartialTick;
    @Shadow @Final private Timer timer;

    @Inject(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
    )
    private void neoforged$onRunTick(CallbackInfo ci) {
        this.realPartialTick = this.pause ? this.pausePartialTick : this.timer.partialTick;
    }

    @Unique private float realPartialTick;
    @Unique
    public float getPartialTick() {
        return this.realPartialTick;
    }
}
