package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * This mixin hooks into applyBiomeDecoration to manually place Alex's Caves features.
 */
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void ac_applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        if (!(level instanceof WorldGenRegion)) {
            return;
        }
        
        ChunkPos chunkPos = chunk.getPos();
        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();
        
        Holder<Biome> acBiomeHolder = null;
        ResourceKey<Biome> acBiome = null;
        int foundY = 0;
        
        for (int y = -64; y <= 64; y += 16) {
            BlockPos samplePos = new BlockPos(centerX, y, centerZ);
            Holder<Biome> biomeHolder = level.getBiome(samplePos);
            
            for (ResourceKey<Biome> key : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
                if (biomeHolder.is(key)) {
                    acBiome = key;
                    acBiomeHolder = biomeHolder;
                    foundY = y;
                    break;
                }
            }
            if (acBiome != null) break;
        }
        
        if (acBiome == null || acBiomeHolder == null) {
            return;
        }
        
        List<HolderSet<PlacedFeature>> biomeFeatures = acBiomeHolder.value().getGenerationSettings().features();
        
        long seed = level.getSeed();
        
        for (int step = 0; step < biomeFeatures.size(); step++) {
            HolderSet<PlacedFeature> featuresForStep = biomeFeatures.get(step);
            
            for (Holder<PlacedFeature> featureHolder : featuresForStep) {
                Optional<ResourceKey<PlacedFeature>> keyOpt = featureHolder.unwrapKey();
                if (keyOpt.isEmpty()) continue;
                
                ResourceKey<PlacedFeature> featureKey = keyOpt.get();
                if (!featureKey.location().getNamespace().equals(AlexsCaves.MODID)) {
                    continue;
                }
                
                try {
                    PlacedFeature feature = featureHolder.value();
                    BlockPos originPos = new BlockPos(centerX, foundY, centerZ);
                    long featureSeed = seed ^ featureKey.location().hashCode() ^ chunkPos.toLong();
                    RandomSource random = RandomSource.create(featureSeed);
                    feature.placeWithBiomeCheck(level, (ChunkGenerator)(Object)this, random, originPos);
                } catch (Exception e) {
                }
            }
        }
    }
}
