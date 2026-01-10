package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ACBiomeRarity {
    private static long lastTestedSeed = 0;
    private static final List<Integer> BIOME_OCTAVES = ImmutableList.of(0);
    private static final PerlinSimplexNoise NOISE_X = new PerlinSimplexNoise(new XoroshiroRandomSource(1234L), BIOME_OCTAVES);
    private static final PerlinSimplexNoise NOISE_Z = new PerlinSimplexNoise(new XoroshiroRandomSource(4321L), BIOME_OCTAVES);
    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);

    private static double biomeSize;
    private static double seperationDistance;
    private static volatile boolean initialized = false;

    public static void init() {
        VORONOI_GENERATOR.setOffsetAmount(AlexsCaves.COMMON_CONFIG.caveBiomeSpacingRandomness.get());
        biomeSize = AlexsCaves.COMMON_CONFIG.caveBiomeMeanWidth.get() * 0.25D;
        seperationDistance = biomeSize + AlexsCaves.COMMON_CONFIG.caveBiomeMeanSeparation.get() * 0.25D;
        initialized = true;
    }
    
    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (VORONOI_GENERATOR) {
                if (!initialized) {
                    init();
                }
            }
        }
    }

    @Nullable
    public static VoronoiGenerator.VoronoiInfo getRareBiomeInfoForQuad(long worldSeed, int x, int z) {
        ensureInitialized();
        
        if (seperationDistance <= 0) {
            return null;
        }
        
        VORONOI_GENERATOR.setSeed(worldSeed);
        double sampleX = x / seperationDistance;
        double sampleZ = z / seperationDistance;
        double positionOffsetX = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * NOISE_X.getValue(sampleX, sampleZ, false);
        double positionOffsetZ = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get() * NOISE_Z.getValue(sampleX, sampleZ, false);
        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);
        if (info.distance() < (biomeSize / seperationDistance)) {
            return info;
        } else {
            return null;
        }
    }

    @Nullable
    public static Vec3 getRareBiomeCenter(VoronoiGenerator.VoronoiInfo voronoiInfo) {
        return voronoiInfo.cellPos().scale(seperationDistance);
    }

    @Nullable
    public static int getRareBiomeOffsetId(VoronoiGenerator.VoronoiInfo voronoiInfo) {
        // Hash ranges from -1 to 1, convert to 0-1 range then multiply by biome count
        // Use Math.min to clamp to valid range (prevents offset = biomeCount when hash = 1.0)
        double normalized = (voronoiInfo.hash() + 1D) * 0.5D; // 0.0 to 1.0
        int biomeCount = BiomeGenerationConfig.getBiomeCount();
        int offset = (int) (normalized * biomeCount);
        // Clamp to valid range [0, biomeCount-1]
        return Math.min(offset, biomeCount - 1);
    }

    public static boolean isQuartInRareBiome(long worldSeed, int x, int z) {
        return ACBiomeRarity.getRareBiomeInfoForQuad(worldSeed, x, z) != null;
    }

    /**
     * Gets the AC biome that would generate at a given block position.
     * This is used by the /locateacbiome command to find biome locations.
     * 
     * @param worldSeed The world seed
     * @param blockX Block X coordinate
     * @param blockZ Block Z coordinate
     * @return The biome key if an AC biome would generate here, null otherwise
     */
    @Nullable
    public static ResourceKey<Biome> getACBiomeForPosition(long worldSeed, int blockX, int blockZ) {
        ensureInitialized();
        
        // Convert block coords to quart coords (biome sampling uses quart coords)
        int quartX = blockX >> 2;
        int quartZ = blockZ >> 2;
        
        VoronoiGenerator.VoronoiInfo voronoiInfo = getRareBiomeInfoForQuad(worldSeed, quartX, quartZ);
        if (voronoiInfo == null) {
            return null;
        }
        
        int rarityOffset = getRareBiomeOffsetId(voronoiInfo);
        
        // Get the biome center for distance checking
        Vec3 biomeCenter = getRareBiomeCenter(voronoiInfo);
        if (biomeCenter == null) {
            return null;
        }
        
        // Convert biome center from quart coords to block coords for distance check
        int centerBlockX = (int) biomeCenter.x * 4;
        int centerBlockZ = (int) biomeCenter.z * 4;
        
        // Find the biome with matching rarity offset AND check distance from spawn
        for (Map.Entry<ResourceKey<Biome>, BiomeGenerationNoiseCondition> entry : BiomeGenerationConfig.BIOMES.entrySet()) {
            if (entry.getValue().getRarityOffset() == rarityOffset) {
                // Check if biome center is far enough from spawn
                int distFromSpawn = entry.getValue().getDistanceFromSpawn();
                if (centerBlockX * centerBlockX + centerBlockZ * centerBlockZ < distFromSpawn * distFromSpawn) {
                    return null; // Too close to spawn
                }
                return entry.getKey();
            }
        }
        
        return null;
    }

    /**
     * Gets the center position of an AC biome region.
     * 
     * @param worldSeed The world seed
     * @param blockX Block X coordinate within the biome region
     * @param blockZ Block Z coordinate within the biome region
     * @return The center position of the biome region, or null if not in an AC biome
     */
    @Nullable
    public static Vec3 getACBiomeCenterForPosition(long worldSeed, int blockX, int blockZ) {
        ensureInitialized();
        
        int quartX = blockX >> 2;
        int quartZ = blockZ >> 2;
        
        VoronoiGenerator.VoronoiInfo voronoiInfo = getRareBiomeInfoForQuad(worldSeed, quartX, quartZ);
        if (voronoiInfo == null) {
            return null;
        }
        
        return getRareBiomeCenter(voronoiInfo);
    }
}
