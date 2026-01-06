package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldOptions.class)
public class WorldGenOptionsMixin {

    @Shadow
    @Final
    private long seed;

    @Inject(method = "seed", at = @At("HEAD"))
    private void ac_onGetSeed(CallbackInfoReturnable<Long> cir) {
        if (!ACWorldSeedHolder.isInitialized() && this.seed != 0) {
            ACWorldSeedHolder.setSeed(this.seed);
            AlexsCaves.LOGGER.info("[AC] WorldGenOptionsMixin: Captured world seed {} from WorldOptions.seed()", this.seed);
        }
    }
}
