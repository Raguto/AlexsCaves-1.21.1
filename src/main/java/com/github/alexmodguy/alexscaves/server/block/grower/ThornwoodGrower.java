package com.github.alexmodguy.alexscaves.server.block.grower;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class ThornwoodGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> THORNWOOD_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "thornwood_tree"));

    public static final TreeGrower GROWER = new TreeGrower(
            "thornwood",
            Optional.empty(),
            Optional.of(THORNWOOD_TREE),
            Optional.empty()
    );
}
