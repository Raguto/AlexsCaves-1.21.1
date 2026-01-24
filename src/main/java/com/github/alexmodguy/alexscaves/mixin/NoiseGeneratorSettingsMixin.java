package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        ResourceKey<Level> dimension = alexscaves$getDimensionForSettings((NoiseGeneratorSettings) (Object) this);
        if (dimension != null) {
            ACWorldSeedHolder.setDimension(dimension);
        }
        if (!this.alexscaves$initialized) {
            SurfaceRules.RuleSource currentRules = cir.getReturnValue();
            if (Level.OVERWORLD.equals(dimension)) {
                this.alexscaves$mergedRules = SurfaceRulesManager.mergeOverworldRules(currentRules);
            }
            this.alexscaves$initialized = true;
        }
        if (Level.OVERWORLD.equals(dimension) && this.alexscaves$mergedRules != null) {
            cir.setReturnValue(this.alexscaves$mergedRules);
        }
    }

    @Unique
    private static ResourceKey<Level> alexscaves$getDimensionForSettings(NoiseGeneratorSettings settings) {
        BlockState defaultBlock = settings.defaultBlock();
        if (defaultBlock == null) {
            return null;
        }
        if (defaultBlock.is(Blocks.NETHERRACK)) {
            return Level.NETHER;
        }
        if (defaultBlock.is(Blocks.END_STONE)) {
            return Level.END;
        }
        if (defaultBlock.is(Blocks.STONE)) {
            return Level.OVERWORLD;
        }
        return null;
    }
}
