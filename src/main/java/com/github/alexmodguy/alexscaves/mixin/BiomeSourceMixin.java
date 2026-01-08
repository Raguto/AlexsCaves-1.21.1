package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements BiomeSourceAccessor {

    @Shadow
    public Supplier<Set<Holder<Biome>>> possibleBiomes;
    
    @Unique
    private Map<ResourceKey<Biome>, Holder<Biome>> ac_map = new HashMap<>();

    @Override
    public void setResourceKeyMap(Map<ResourceKey<Biome>, Holder<Biome>> map) {
        this.ac_map = map;
    }

    @Override
    public Map<ResourceKey<Biome>, Holder<Biome>> getResourceKeyMap() {
        return ac_map;
    }

    @Override
    public void expandBiomesWith(Set<Holder<Biome>> newGenBiomes) {
        // Don't actually expand possibleBiomes - this causes feature order cycle issues on dedicated servers
        // The biome injection via MultiNoiseBiomeSourceMixin handles returning AC biomes when appropriate
        // Adding them to possibleBiomes causes the ChunkGenerator's FeatureSorter to try to sort
        // AC biome features alongside vanilla features, which can cause cycle detection failures
        AlexsCaves.LOGGER.debug("[AC] expandBiomesWith called with {} biomes - skipping to avoid feature cycle issues", newGenBiomes.size());
    }
}
