package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldOptions.class)
public class WorldGenOptionsMixin {

    @Shadow
    @Final
    private long seed;

    @Unique
    private static boolean ac_loggedOnce = false;

    @Inject(method = "seed", at = @At("HEAD"))
    private void ac_onGetSeed(CallbackInfoReturnable<Long> cir) {
        if (this.seed != 0) {
            ACWorldSeedHolder.setSeed(this.seed);
            if (!ac_loggedOnce) {
                ac_loggedOnce = true;
                AlexsCaves.LOGGER.info("[AC] WorldGenOptionsMixin: Set world seed {} in ACWorldSeedHolder (thread: {})", 
                    this.seed, Thread.currentThread().getName());
            }
        }
    }
}
