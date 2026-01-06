package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Shadow
    @Final
    ServerLevel level;

    @Shadow
    public ChunkGenerator getGenerator() {
        throw new AssertionError();
    }

    @Unique
    private static boolean ac_loggedOnce = false;

    @Inject(at = @At("HEAD"), method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;")
    private void ac_getChunk(int x, int z, ChunkStatus status, boolean load, CallbackInfoReturnable<ChunkAccess> cir) {
        BiomeSource biomeSource = getGenerator().getBiomeSource();
        if (biomeSource instanceof MultiNoiseBiomeSourceAccessor accessor) {
            accessor.setLastSampledSeed(level.getSeed());
            accessor.setLastSampledDimension(level.dimension());
            if (!ac_loggedOnce) {
                AlexsCaves.LOGGER.info("[AC DEBUG] ServerChunkCacheMixin: Set seed {} and dimension {} on biome source {}", 
                    level.getSeed(), level.dimension().location(), biomeSource.getClass().getSimpleName());
                ac_loggedOnce = true;
            }
        } else if (!ac_loggedOnce) {
            AlexsCaves.LOGGER.warn("[AC DEBUG] ServerChunkCacheMixin: BiomeSource {} is NOT MultiNoiseBiomeSourceAccessor!", 
                biomeSource.getClass().getName());
            ac_loggedOnce = true;
        }
    }
}
