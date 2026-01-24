package com.github.alexmodguy.alexscaves.server.level.surface;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACSurfaceRuleConditionRegistry {

    public static final DeferredRegister<MapCodec<? extends SurfaceRules.ConditionSource>> DEF_REG = DeferredRegister.create(Registries.MATERIAL_CONDITION, AlexsCaves.MODID);

    public static final DeferredHolder<MapCodec<? extends SurfaceRules.ConditionSource>, MapCodec<SimplexConditionSource>> AC_SIMPLEX_CONDITION = DEF_REG.register("ac_simplex", () -> SimplexConditionSource.CODEC);
    public static final DeferredHolder<MapCodec<? extends SurfaceRules.ConditionSource>, MapCodec<ACBiomeConditionSource>> AC_BIOME_CONDITION = DEF_REG.register("ac_biome", () -> ACBiomeConditionSource.CODEC);

    public static SurfaceRules.ConditionSource simplexCondition(float noiseMin, float noiseMax, float noiseScale, float yScale, int offsetType) {
        return new SimplexConditionSource(noiseMin, noiseMax, noiseScale, yScale, offsetType);
    }
    
    public static SurfaceRules.ConditionSource acBiomeCondition(int rarityOffset) {
        return new ACBiomeConditionSource(rarityOffset);
    }

    public record SimplexConditionSource(float noiseMin, float noiseMax, float noiseScale, float yScale,
                                          int offsetType) implements SurfaceRules.ConditionSource {
        public static final MapCodec<SimplexConditionSource> CODEC = RecordCodecBuilder.mapCodec((group) -> {
            return group.group(Codec.floatRange(-1F, 1F).fieldOf("noise_min").forGetter(SimplexConditionSource::noiseMin), Codec.floatRange(-1F, 1F).fieldOf("noise_max").forGetter(SimplexConditionSource::noiseMax), Codec.floatRange(1F, 10000F).fieldOf("noise_scale").forGetter(SimplexConditionSource::noiseScale), Codec.floatRange(0F, 10000F).fieldOf("y_scale").forGetter(SimplexConditionSource::yScale), Codec.intRange(0, 128).fieldOf("offset_type").forGetter(SimplexConditionSource::offsetType)).apply(group, SimplexConditionSource::new);
        });

        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return KeyDispatchDataCodec.of(CODEC);
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context contextIn) {
            class NoiseCondition implements SurfaceRules.Condition {

                private SurfaceRules.Context context;

                NoiseCondition(SurfaceRules.Context context) {
                    this.context = context;
                }

                public boolean test() {
                    ResourceKey<Level> dimension = ACWorldSeedHolder.getDimension();
                    if (dimension != null && !Level.OVERWORLD.equals(dimension)) {
                        return false;
                    }
                    int x = context.blockX;
                    int y = context.blockY;
                    int z = context.blockZ;
                    double f = ACMath.sampleNoise3D(x + (offsetType * 1000), (int) ((y * yScale + offsetType * 2000)), z - (offsetType * 3000), SimplexConditionSource.this.noiseScale);
                    return f > SimplexConditionSource.this.noiseMin && f <= SimplexConditionSource.this.noiseMax;
                }
            }
            return new NoiseCondition(contextIn);
        }
    }
    

    public record ACBiomeConditionSource(int rarityOffset) implements SurfaceRules.ConditionSource {
        private static final int MAX_Y_LEVEL = 50;
        
        public static final MapCodec<ACBiomeConditionSource> CODEC = RecordCodecBuilder.mapCodec((group) -> {
            return group.group(Codec.intRange(0, 100).fieldOf("rarity_offset").forGetter(ACBiomeConditionSource::rarityOffset)).apply(group, ACBiomeConditionSource::new);
        });

        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return KeyDispatchDataCodec.of(CODEC);
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context contextIn) {
            final ResourceKey<Level> dimension = ACWorldSeedHolder.getDimension();
            class ACBiomeCondition implements SurfaceRules.Condition {

                private SurfaceRules.Context context;

                ACBiomeCondition(SurfaceRules.Context context) {
                    this.context = context;
                }

                public boolean test() {
                    if (!Level.OVERWORLD.equals(dimension)) {
                        return false;
                    }
                    int y = context.blockY;
                    if (y > MAX_Y_LEVEL) {
                        return false;
                    }
                    
                    long seed = ACWorldSeedHolder.getSeed();
                    if (seed == 0) {
                        return false;
                    }
                    
                    int x = context.blockX;
                    int z = context.blockZ;
                    int quartX = x >> 2;
                    int quartZ = z >> 2;
                    
                    VoronoiGenerator.VoronoiInfo info = ACBiomeRarity.getRareBiomeInfoForQuad(seed, quartX, quartZ);
                    if (info == null) {
                        return false;
                    }
                    
                    int foundOffset = ACBiomeRarity.getRareBiomeOffsetId(info);
                    if (foundOffset != ACBiomeConditionSource.this.rarityOffset) {
                        return false;
                    }

                    ResourceKey<Biome> biomeAtPos = ACBiomeRarity.getACBiomeForPosition(seed, x, z);
                    if (biomeAtPos == null) {
                        return false;
                    }

                    BiomeGenerationNoiseCondition noiseCondition = BiomeGenerationConfig.BIOMES.get(biomeAtPos);
                    return noiseCondition != null && noiseCondition.getRarityOffset() == ACBiomeConditionSource.this.rarityOffset;
                }
            }
            return new ACBiomeCondition(contextIn);
        }
    }
}
