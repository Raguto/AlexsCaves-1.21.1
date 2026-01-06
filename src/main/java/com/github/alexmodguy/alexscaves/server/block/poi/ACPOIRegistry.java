package com.github.alexmodguy.alexscaves.server.block.poi;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class ACPOIRegistry {

    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, AlexsCaves.MODID);
    public static final DeferredHolder<PoiType, PoiType> ATTRACTING_MAGNETS = DEF_REG.register("attracting_magnets", () -> new PoiType(getAllAttractingMagnets(), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> REPELLING_MAGNETS = DEF_REG.register("repelling_magnets", () -> new PoiType(getAllRepellingMagnets(), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> NUCLEAR_SIREN = DEF_REG.register("nuclear_siren", () -> new PoiType(getAllStatesOf(ACBlockRegistry.NUCLEAR_SIREN.get()), 0, 6));
    public static final DeferredHolder<PoiType, PoiType> NUCLEAR_FURNACE = DEF_REG.register("nuclear_furnace", () -> new PoiType(getAllStatesOf(ACBlockRegistry.NUCLEAR_FURNACE.get()), 0, 6));
    public static final DeferredHolder<PoiType, PoiType> ABYSSAL_ALTAR = DEF_REG.register("abyssal_altar", () -> new PoiType(getAllStatesOf(ACBlockRegistry.ABYSSAL_ALTAR.get()), 0, 6));
    public static final DeferredHolder<PoiType, PoiType> MOTH_BALL = DEF_REG.register("moth_ball", () -> new PoiType(getAllStatesOf(ACBlockRegistry.MOTH_BALL.get()), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> SUNDROP = DEF_REG.register("sundrop", () -> new PoiType(getAllStatesOf(ACBlockRegistry.SUNDROP.get()), 32, 6));
    public static final DeferredHolder<PoiType, PoiType> CONVERSION_CRUCIBLE = DEF_REG.register("conversion_crucible", () -> new PoiType(getAllStatesOf(ACBlockRegistry.CONVERSION_CRUCIBLE.get()), 0, 6));
    public static final DeferredHolder<PoiType, PoiType> GINGERBARREL = DEF_REG.register("gingerbarrel", () -> new PoiType(getAllStatesOf(ACBlockRegistry.GINGERBARREL.get()), 0, 6));

    private static Set<BlockState> getAllAttractingMagnets() {
        ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
        builder.addAll(getAllStatesOf(ACBlockRegistry.SCARLET_NEODYMIUM_NODE.get()));
        builder.addAll(getAllStatesOf(ACBlockRegistry.SCARLET_NEODYMIUM_PILLAR.get()));
        builder.addAll(getAllStatesOf(ACBlockRegistry.BLOCK_OF_SCARLET_NEODYMIUM.get()));
        return builder.build();
    }

    private static Set<BlockState> getAllRepellingMagnets() {
        ImmutableSet.Builder<BlockState> builder = ImmutableSet.builder();
        builder.addAll(getAllStatesOf(ACBlockRegistry.AZURE_NEODYMIUM_NODE.get()));
        builder.addAll(getAllStatesOf(ACBlockRegistry.AZURE_NEODYMIUM_PILLAR.get()));
        builder.addAll(getAllStatesOf(ACBlockRegistry.BLOCK_OF_AZURE_NEODYMIUM.get()));
        return builder.build();
    }

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
}
