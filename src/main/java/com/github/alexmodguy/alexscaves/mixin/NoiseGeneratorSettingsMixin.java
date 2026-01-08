package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin injects custom surface rules from Citadel's SurfaceRulesManager into the world generation.
 * Citadel 1.21.1 has this mixin in its source but it's not registered in the mixin config,
 * so we add it here to ensure Alex's Caves surface rules are applied.
 */
@Mixin(value = NoiseGeneratorSettings.class, priority = 500)
public class NoiseGeneratorSettingsMixin {
    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    @Unique
    private boolean alexscaves$mergedSurfaceRules = false;

    @Inject(method = "surfaceRule", at = @At("HEAD"))
    private void alexscaves$surfaceRule(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!this.alexscaves$mergedSurfaceRules) {
            this.surfaceRule = SurfaceRulesManager.mergeOverworldRules(surfaceRule);
            this.alexscaves$mergedSurfaceRules = true;
        }
    }
}
