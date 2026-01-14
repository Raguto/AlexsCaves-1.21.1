package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin injects custom surface rules from Citadel's SurfaceRulesManager into the world generation.
 * Uses priority 1500 to run after Citadel's mixin (priority 500) to ensure rules are merged.
 */
@Mixin(value = NoiseGeneratorSettings.class, priority = 1500)
public class NoiseGeneratorSettingsMixin {
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private SurfaceRules.RuleSource alexscaves$mergedRules = null;

    @Unique
    private boolean alexscaves$initialized = false;

    @Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
    private void alexscaves$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!this.alexscaves$initialized) {
            SurfaceRules.RuleSource currentRules = cir.getReturnValue();
            this.alexscaves$mergedRules = SurfaceRulesManager.mergeOverworldRules(currentRules);
            this.alexscaves$initialized = true;
        }
        if (this.alexscaves$mergedRules != null) {
            cir.setReturnValue(this.alexscaves$mergedRules);
        }
    }
}
