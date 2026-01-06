package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACStructureRegistry {

    public static final DeferredRegister<StructureType<?>> DEF_REG = DeferredRegister.create(Registries.STRUCTURE_TYPE, AlexsCaves.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<UndergroundCabinStructure>> UNDERGROUND_CABIN = DEF_REG.register("underground_cabin", () -> () -> UndergroundCabinStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<FerrocaveStructure>> FERROCAVE = DEF_REG.register("ferrocave", () -> () -> FerrocaveStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<VolcanoStructure>> VOLCANO = DEF_REG.register("volcano", () -> () -> VolcanoStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<DinoBowlStructure>> DINO_BOWL = DEF_REG.register("dino_bowl", () -> () -> DinoBowlStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<AcidPitStructure>> ACID_PIT = DEF_REG.register("acid_pit", () -> () -> AcidPitStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<OceanTrenchStructure>> OCEAN_TRENCH = DEF_REG.register("ocean_trench", () -> () -> OceanTrenchStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<AbyssalRuinsStructure>> ABYSSAL_RUINS = DEF_REG.register("abyssal_ruins", () -> () -> AbyssalRuinsStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<ForlornCanyonStructure>> FORLORN_CANYON = DEF_REG.register("forlorn_canyon", () -> () -> ForlornCanyonStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<ForlornBridgeStructure>> FORLORN_BRIDGE = DEF_REG.register("forlorn_bridge", () -> () -> ForlornBridgeStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<CakeCaveStructure>> CAKE_CAVE = DEF_REG.register("cake_cave", () -> () -> CakeCaveStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<SodaBottleStructure>> SODA_BOTTLE = DEF_REG.register("soda_bottle", () -> () -> SodaBottleStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<DonutArchStructure>> DONUT_ARCH = DEF_REG.register("donut_arch", () -> () -> DonutArchStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<LicowitchTowerStructure>> LICOWITCH_TOWER = DEF_REG.register("licowitch_tower", () -> () -> LicowitchTowerStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<GingerbreadTownStructure>> GINGERBREAD_TOWN = DEF_REG.register("gingerbread_town", () -> () -> GingerbreadTownStructure.CODEC);
}
