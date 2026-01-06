package com.github.alexmodguy.alexscaves.server.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArchaicVinePlantBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<ArchaicVinePlantBlock> CODEC = simpleCodec(props -> new ArchaicVinePlantBlock());
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public ArchaicVinePlantBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).randomTicks().noCollission().instabreak().sound(SoundType.VINE), Direction.DOWN, SHAPE, false);
    }

    @Override
    public MapCodec<? extends GrowingPlantBodyBlock> codec() {
        return CODEC;
    }

    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ACBlockRegistry.ARCHAIC_VINE.get();
    }
}
