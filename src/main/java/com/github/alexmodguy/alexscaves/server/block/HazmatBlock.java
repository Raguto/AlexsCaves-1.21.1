package com.github.alexmodguy.alexscaves.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class HazmatBlock extends RotatedPillarBlock {

    public HazmatBlock() {
        super(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.5F, 12.0F).sound(ACSoundTypes.HAZMAT_BLOCK));
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.UNPASSABLE_RAIL;
    }

    @Override
    public PathType getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, PathType originalType) {
        return PathType.UNPASSABLE_RAIL;
    }
}
