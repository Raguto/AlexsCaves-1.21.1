package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    private float darkenWorldAmount;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        if (((ClientProxy) AlexsCaves.PROXY).renderNukeSkyDarkFor > 0 && darkenWorldAmount < 1.0F) {
            darkenWorldAmount = Math.min(darkenWorldAmount + 0.3F, 1.0F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V",
                    shift = At.Shift.AFTER
            )
    )
    public void ac_render(DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        ((ClientProxy) AlexsCaves.PROXY).preScreenRender(deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
