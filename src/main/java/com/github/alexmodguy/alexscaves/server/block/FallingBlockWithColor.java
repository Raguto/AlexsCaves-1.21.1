package com.github.alexmodguy.alexscaves.server.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

class FallingBlockWithColor extends FallingBlock {
    public static final MapCodec<FallingBlockWithColor> CODEC = simpleCodec(props -> new FallingBlockWithColor(props, 0));

    private int dustColor;

    public FallingBlockWithColor(BlockBehaviour.Properties properties, int dustColor) {
        super(properties);
        this.dustColor = dustColor;
    }

    @Override
    public MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return dustColor;
    }

}
