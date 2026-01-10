package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * This mixin sets the world seed on the biome source during chunk generation.
 * This is the 1.21.1 equivalent of the original ChunkStatusMixin from 1.20.1.
 * 
 * In 1.20.1, this hooked into ChunkStatus.generate()
 * In 1.21.1, chunk generation was refactored to ChunkStatusTasks
 */
@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/world/level/chunk/status/ChunkStatusTasks;generateNoise(Lnet/minecraft/world/level/chunk/status/WorldGenContext;Lnet/minecraft/world/level/chunk/status/ChunkStep;Lnet/minecraft/util/StaticCache2D;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;")
    private static void ac_fillFromNoise(WorldGenContext worldGenContext, ChunkStep step, StaticCache2D<GenerationChunkHolder> cache, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (worldGenContext.generator().getBiomeSource() instanceof MultiNoiseBiomeSourceAccessor multiNoiseBiomeSourceAccessor) {
            multiNoiseBiomeSourceAccessor.setLastSampledSeed(worldGenContext.level().getSeed());
            multiNoiseBiomeSourceAccessor.setLastSampledDimension(worldGenContext.level().dimension());
        }
    }
}
