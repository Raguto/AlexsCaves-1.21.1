package com.github.alexmodguy.alexscaves.server.block.grower;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class AncientTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "ancient_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_ANCIENT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "giant_ancient_tree"));

    public static final TreeGrower GROWER = new TreeGrower(
            "ancient",
            Optional.of(GIANT_ANCIENT_TREE),
            Optional.of(ANCIENT_TREE),
            Optional.empty()
    );
}
