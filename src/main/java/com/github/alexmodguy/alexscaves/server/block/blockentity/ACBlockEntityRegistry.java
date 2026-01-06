package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AlexsCaves.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VolcanicCoreBlockEntity>> VOLCANIC_CORE = DEF_REG.register("volcanic_core", () -> BlockEntityType.Builder.of(VolcanicCoreBlockEntity::new, ACBlockRegistry.VOLCANIC_CORE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagnetBlockEntity>> MAGNET = DEF_REG.register("magnet", () -> BlockEntityType.Builder.of(MagnetBlockEntity::new, ACBlockRegistry.SCARLET_MAGNET.get(), ACBlockRegistry.AZURE_MAGNET.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeslaBulbBlockEntity>> TESLA_BULB = DEF_REG.register("tesla_bulb", () -> BlockEntityType.Builder.of(TeslaBulbBlockEntity::new, ACBlockRegistry.TESLA_BULB.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HologramProjectorBlockEntity>> HOLOGRAM_PROJECTOR = DEF_REG.register("hologram_projector", () -> BlockEntityType.Builder.of(HologramProjectorBlockEntity::new, ACBlockRegistry.HOLOGRAM_PROJECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuarryBlockEntity>> QUARRY = DEF_REG.register("quarry", () -> BlockEntityType.Builder.of(QuarryBlockEntity::new, ACBlockRegistry.QUARRY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmbersolBlockEntity>> AMBERSOL = DEF_REG.register("ambersol", () -> BlockEntityType.Builder.of(AmbersolBlockEntity::new, ACBlockRegistry.AMBERSOL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmberMonolithBlockEntity>> AMBER_MONOLITH = DEF_REG.register("amber_monolith", () -> BlockEntityType.Builder.of(AmberMonolithBlockEntity::new, ACBlockRegistry.AMBER_MONOLITH.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeothermalVentBlockEntity>> GEOTHERMAL_VENT = DEF_REG.register("geothermal_vent", () -> BlockEntityType.Builder.of(GeothermalVentBlockEntity::new, ACBlockRegistry.GEOTHERMAL_VENT.get(), ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get(), ACBlockRegistry.GEOTHERMAL_VENT_THIN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NuclearFurnaceBlockEntity>> NUCLEAR_FURNACE = DEF_REG.register("nuclear_furnace", () -> BlockEntityType.Builder.of(NuclearFurnaceBlockEntity::new, ACBlockRegistry.NUCLEAR_FURNACE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SirenLightBlockEntity>> SIREN_LIGHT = DEF_REG.register("siren_light", () -> BlockEntityType.Builder.of(SirenLightBlockEntity::new, ACBlockRegistry.SIREN_LIGHT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NuclearSirenBlockEntity>> NUCLEAR_SIREN = DEF_REG.register("nuclear_siren", () -> BlockEntityType.Builder.of(NuclearSirenBlockEntity::new, ACBlockRegistry.NUCLEAR_SIREN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalBarrelBlockEntity>> METAL_BARREL = DEF_REG.register("metal_barrel", () -> BlockEntityType.Builder.of(MetalBarrelBlockEntity::new, ACBlockRegistry.METAL_BARREL.get(), ACBlockRegistry.RUSTY_BARREL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AbyssalAltarBlockEntity>> ABYSSAL_ALTAR = DEF_REG.register("abyssal_altar", () -> BlockEntityType.Builder.of(AbyssalAltarBlockEntity::new, ACBlockRegistry.ABYSSAL_ALTAR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperValveBlockEntity>> COPPER_VALVE = DEF_REG.register("copper_valve", () -> BlockEntityType.Builder.of(CopperValveBlockEntity::new, ACBlockRegistry.COPPER_VALVE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnigmaticEngineBlockEntity>> ENIGMATIC_ENGINE = DEF_REG.register("enigmatic_engine", () -> BlockEntityType.Builder.of(EnigmaticEngineBlockEntity::new, ACBlockRegistry.ENIGMATIC_ENGINE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeholderBlockEntity>> BEHOLDER = DEF_REG.register("beholder", () -> BlockEntityType.Builder.of(BeholderBlockEntity::new, ACBlockRegistry.BEHOLDER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GobthumperBlockEntity>> GOBTHUMPER = DEF_REG.register("gobthumper", () -> BlockEntityType.Builder.of(GobthumperBlockEntity::new, ACBlockRegistry.GOBTHUMPER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConversionCrucibleBlockEntity>> CONVERSION_CRUCIBLE = DEF_REG.register("conversion_crucible", () -> BlockEntityType.Builder.of(ConversionCrucibleBlockEntity::new, ACBlockRegistry.CONVERSION_CRUCIBLE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GingerbarrelBlockEntity>> GINGERBARREL = DEF_REG.register("gingerbarrel", () -> BlockEntityType.Builder.of(GingerbarrelBlockEntity::new, ACBlockRegistry.GINGERBARREL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConfectionOvenBlockEntity>> CONFECTION_OVEN = DEF_REG.register("confection_oven", () -> BlockEntityType.Builder.of(ConfectionOvenBlockEntity::new, ACBlockRegistry.CONFECTION_OVEN.get()).build(null));

    public static void expandVanillaDefinitions() {
    }
}
