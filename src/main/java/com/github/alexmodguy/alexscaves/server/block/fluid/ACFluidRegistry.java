package com.github.alexmodguy.alexscaves.server.block.fluid;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ACFluidRegistry {
    public static final DeferredRegister<FluidType> FLUID_TYPE_DEF_REG = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, AlexsCaves.MODID);
    public static final DeferredRegister<Fluid> FLUID_DEF_REG = DeferredRegister.create(Registries.FLUID, AlexsCaves.MODID);

    private static BaseFlowingFluid.Properties acidProperties() {
        return new BaseFlowingFluid.Properties(ACID_FLUID_TYPE, ACID_FLUID_SOURCE, ACID_FLUID_FLOWING).bucket(ACItemRegistry.ACID_BUCKET).block((Supplier<? extends LiquidBlock>)(Supplier<?>) ACBlockRegistry.ACID);
    }

    private static BaseFlowingFluid.Properties purpleSodaProperties() {
        return new BaseFlowingFluid.Properties(PURPLE_SODA_FLUID_TYPE, PURPLE_SODA_FLUID_SOURCE, PURPLE_SODA_FLUID_FLOWING).bucket(ACItemRegistry.PURPLE_SODA_BUCKET).block((Supplier<? extends LiquidBlock>)(Supplier<?>) ACBlockRegistry.PURPLE_SODA);
    }

    public static final DeferredHolder<FluidType, FluidType> ACID_FLUID_TYPE = FLUID_TYPE_DEF_REG.register("acid", () -> new AcidFluidType(FluidType.Properties.create().lightLevel(5).density(1024).viscosity(1024).pathType(PathType.LAVA).adjacentPathType(PathType.DANGER_OTHER).sound(SoundActions.BUCKET_EMPTY, ACSoundRegistry.ACID_UNSUBMERGE.get()).sound(SoundActions.BUCKET_FILL, ACSoundRegistry.ACID_SUBMERGE.get())));
    public static final DeferredHolder<Fluid, FlowingFluid> ACID_FLUID_SOURCE = FLUID_DEF_REG.register("acid", () -> new BaseFlowingFluid.Source(acidProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> ACID_FLUID_FLOWING = FLUID_DEF_REG.register("acid_flowing", () -> new BaseFlowingFluid.Flowing(acidProperties()));

    public static final DeferredHolder<FluidType, FluidType> PURPLE_SODA_FLUID_TYPE = FLUID_TYPE_DEF_REG.register("purple_soda", () -> new PurpleSodaFluidType(FluidType.Properties.create().density(1000).viscosity(1000).pathType(PathType.WATER).adjacentPathType(PathType.WATER_BORDER).sound(SoundActions.BUCKET_EMPTY, ACSoundRegistry.PURPLE_SODA_UNSUBMERGE.get()).sound(SoundActions.BUCKET_FILL, ACSoundRegistry.PURPLE_SODA_SUBMERGE.get())));
    public static final DeferredHolder<Fluid, FlowingFluid> PURPLE_SODA_FLUID_SOURCE = FLUID_DEF_REG.register("purple_soda", () -> new BaseFlowingFluid.Source(purpleSodaProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> PURPLE_SODA_FLUID_FLOWING = FLUID_DEF_REG.register("purple_soda_flowing", () -> new BaseFlowingFluid.Flowing(purpleSodaProperties()));

    public static void postInit() {
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(),
                fluidState -> Blocks.MUD.defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(),
                fluidState -> ACBlockRegistry.RADROCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.WATER_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> Blocks.MUD.defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.RADROCK.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.WATER_TYPE.value(),
                fluidState -> ACBlockRegistry.BLUE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                NeoForgeMod.LAVA_TYPE.value(),
                fluidState -> ACBlockRegistry.ORANGE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(PURPLE_SODA_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ACID_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.GREEN_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.WATER_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.BLUE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.ORANGE_ROCK_CANDY.get().defaultBlockState()
        ));
        FluidInteractionRegistry.addInteraction(ACID_FLUID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                PURPLE_SODA_FLUID_TYPE.get(),
                fluidState -> ACBlockRegistry.GREEN_ROCK_CANDY.get().defaultBlockState()
        ));
    }
}
