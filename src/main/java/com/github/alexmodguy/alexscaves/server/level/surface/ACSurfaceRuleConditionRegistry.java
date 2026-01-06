package com.github.alexmodguy.alexscaves.server.level.surface;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACSurfaceRuleConditionRegistry {

    public static final DeferredRegister<MapCodec<? extends SurfaceRules.ConditionSource>> DEF_REG = DeferredRegister.create(Registries.MATERIAL_CONDITION, AlexsCaves.MODID);

    public static final DeferredHolder<MapCodec<? extends SurfaceRules.ConditionSource>, MapCodec<SimplexConditionSource>> AC_SIMPLEX_CONDITION = DEF_REG.register("ac_simplex", () -> SimplexConditionSource.CODEC);

    public static SurfaceRules.ConditionSource simplexCondition(float noiseMin, float noiseMax, float noiseScale, float yScale, int offsetType) {
        return new SimplexConditionSource(noiseMin, noiseMax, noiseScale, yScale, offsetType);
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
                    // Access the blockX, blockY, blockZ fields directly (made public via access transformer)
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
}
