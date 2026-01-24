package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * This mixin injects Alex's Caves biomes into the world generation.
 * It intercepts biome lookups and returns AC biomes when the voronoi noise
 * and depth conditions are met.
 * 
 * Uses static holders (ACWorldSeedHolder, ACBiomeMapHolder) to get seed and biome map
 * because biome sampling can happen on worker threads with different biome source instances
 * than the ones initialized in onServerAboutToStart.
 */
@Mixin(value = MultiNoiseBiomeSource.class, priority = -69420)
public class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccessor {

    @Unique
    private long lastSampledWorldSeed;

    @Unique
    private ResourceKey<Level> lastSampledDimension;

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            cancellable = true
    )
    private void ac_getNoiseBiomeCoords(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {

        long seed = lastSampledWorldSeed != 0 ? lastSampledWorldSeed
            : (ACWorldSeedHolder.isInitialized() ? ACWorldSeedHolder.getSeed() : 0L);
        ResourceKey<Level> dimension = lastSampledDimension != null ? lastSampledDimension
            : (ACWorldSeedHolder.isInitialized() ? ACWorldSeedHolder.getDimension() : null);

        if (dimension != null) {
            ACWorldSeedHolder.setDimension(dimension);
        }

        if (dimension == null || !dimension.equals(Level.OVERWORLD)) {
            return;
        }

        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = null;
        if (ACBiomeMapHolder.isInitialized()) {
            biomeMap = ACBiomeMapHolder.getBiomeMap();
        }
        if (biomeMap == null || biomeMap.isEmpty()) {
            biomeMap = ((BiomeSourceAccessor) this).getResourceKeyMap();
        }
        
        // Skip if not initialized
        if (seed == 0 || biomeMap == null || biomeMap.isEmpty()) {
            return;
        }

        ac_ensureBiomesExpanded(biomeMap);
        
        // Get voronoi info for this position
        VoronoiGenerator.VoronoiInfo voronoiInfo = ACBiomeRarity.getRareBiomeInfoForQuad(seed, x, z);
        if (voronoiInfo != null) {
            float unquantizedDepth = Climate.unquantizeCoord(sampler.sample(x, y, z).depth());
            int foundRarityOffset = ACBiomeRarity.getRareBiomeOffsetId(voronoiInfo);

            synchronized (BiomeGenerationConfig.BIOMES_LOCK) {
                for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> condition : BiomeGenerationConfig.BIOMES.entrySet()) {
                    if (foundRarityOffset == condition.getValue().getRarityOffset() &&
                        condition.getValue().test(x, y, z, unquantizedDepth, sampler, dimension, voronoiInfo)) {

                        if (condition.getKey() == ACBiomeRegistry.ABYSSAL_CHASM) {
                            Climate.TargetPoint localPoint = sampler.sample(x, y, z);
                            float localContinentalness = Climate.unquantizeCoord(localPoint.continentalness());
                            if (localContinentalness > -0.5F) {
                                continue; // Skip this biome, let vanilla handle it
                            }
                        }

                        Holder<Biome> biomeHolder = biomeMap.get(condition.getKey());
                        if (biomeHolder != null) {
                            cir.setReturnValue(biomeHolder);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void ac_ensureBiomesExpanded(Map<ResourceKey<Biome>, Holder<Biome>> biomeMap) {
        if (!(this instanceof BiomeSourceAccessor accessor)) {
            return;
        }
        if (biomeMap == null || biomeMap.isEmpty()) {
            return;
        }

        ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
        for (ResourceKey<Biome> biomeKey : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
            Holder<Biome> holder = biomeMap.get(biomeKey);
            if (holder != null) {
                builder.add(holder);
            }
        }

        ImmutableSet<Holder<Biome>> acBiomes = builder.build();
        if (!acBiomes.isEmpty()) {
            accessor.expandBiomesWith(acBiomes);
        }
    }

    @Override
    public void setLastSampledSeed(long seed) {
        lastSampledWorldSeed = seed;
        ACWorldSeedHolder.setSeed(seed);
    }

    @Override
    public void setLastSampledDimension(ResourceKey<Level> dimension) {
        lastSampledDimension = dimension;
        ACWorldSeedHolder.setDimension(dimension);
    }
}
