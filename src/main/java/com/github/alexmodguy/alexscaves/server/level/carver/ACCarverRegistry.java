package com.github.alexmodguy.alexscaves.server.level.carver;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACCarverRegistry {

    public static final DeferredRegister<WorldCarver<?>> DEF_REG = DeferredRegister.create(net.minecraft.core.registries.Registries.CARVER, AlexsCaves.MODID);

    //Unused for now.
    public static DeferredHolder<WorldCarver<?>, WaterBubbleCarver> WATER_BUBBLE_CARVER = DEF_REG.register("water_bubble_carver", () -> new WaterBubbleCarver(CaveCarverConfiguration.CODEC));

}
