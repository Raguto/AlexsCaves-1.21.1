package com.github.alexmodguy.alexscaves.server.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class ACDummyBiomeSource extends BiomeSource {

    public static final MapCodec<ACDummyBiomeSource> CODEC = MapCodec.unit(ACDummyBiomeSource::new);

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        // In 1.21.1, biomes are only available through registry access
        // Return empty stream as this is a dummy biome source
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int p_204238_, int p_204239_, int p_204240_, Climate.Sampler p_204241_) {
        return null;
    }
}
