package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Util.class)
public class UtilMixin {

    @Inject(
            method = "logAndPauseIfInIde(Ljava/lang/String;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ac_suppressFarChunkError(String message, CallbackInfo ci) {
        if (message != null && message.contains("Detected setBlock in a far chunk")) {
            ci.cancel();
        }
    }
}
