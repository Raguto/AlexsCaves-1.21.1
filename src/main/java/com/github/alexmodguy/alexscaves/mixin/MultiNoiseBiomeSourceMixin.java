package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
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

@Mixin(value = MultiNoiseBiomeSource.class, priority = -69420)
public class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccessor {

    private long lastSampledWorldSeed;

    private ResourceKey<Level> lastSampledDimension;

    @Unique
    private static boolean ac_loggedDebug = false;
    
    @Unique
    private static int ac_injectCount = 0;

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            cancellable = true
    )
    private void ac_getNoiseBiomeCoords(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        long seedToUse = ACWorldSeedHolder.isInitialized() ? ACWorldSeedHolder.getSeed() : lastSampledWorldSeed;
        ResourceKey<Level> dimensionToUse = ACWorldSeedHolder.isInitialized() ? ACWorldSeedHolder.getDimension() : lastSampledDimension;
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = ACBiomeMapHolder.getBiomeMap();
        
        if (!ac_loggedDebug) {
            ac_loggedDebug = true;
            AlexsCaves.LOGGER.info("Biome injection active: seed={}, biomes={}, holders={}", 
                seedToUse, BiomeGenerationConfig.BIOMES.size(), biomeMap != null ? biomeMap.size() : 0);
        }
        
        if (seedToUse == 0 || biomeMap == null || biomeMap.isEmpty()) {
            return;
        }
        
        if (BiomeGenerationConfig.BIOMES.isEmpty()) {
            return;
        }
        
        VoronoiGenerator.VoronoiInfo voronoiInfo = ACBiomeRarity.getRareBiomeInfoForQuad(seedToUse, x, z);
        if(voronoiInfo != null){
            float unquantizedDepth = Climate.unquantizeCoord(sampler.sample(x, y, z).depth());
            int foundRarityOffset = ACBiomeRarity.getRareBiomeOffsetId(voronoiInfo);
            
            for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> condition : BiomeGenerationConfig.BIOMES.entrySet()) {
                if (foundRarityOffset == condition.getValue().getRarityOffset() && condition.getValue().test(x, y, z, unquantizedDepth, sampler, dimensionToUse, voronoiInfo)) {
                    Holder<Biome> biomeHolder = biomeMap.get(condition.getKey());
                    if (biomeHolder != null) {
                        cir.setReturnValue(biomeHolder);
                        return;
                    }
                }
            }
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
