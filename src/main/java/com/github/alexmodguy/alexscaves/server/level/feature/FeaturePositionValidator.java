package com.github.alexmodguy.alexscaves.server.level.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FeaturePositionValidator {

    public static boolean isBiome(@Nonnull FeaturePlaceContext<?> context, @Nonnull ResourceKey<Biome> biomeResourceKey) {
        int j = context.level().getHeight(Heightmap.Types.OCEAN_FLOOR, context.origin().getX(), context.origin().getZ());
        BlockPos samplePos = Objects.requireNonNull(context.origin().atY(Math.min(context.level().getMinBuildHeight(), j - 30)), "samplePos");
        return context.level().getBiome(samplePos).is(Objects.requireNonNull(biomeResourceKey, "biomeResourceKey"));
    }

}
