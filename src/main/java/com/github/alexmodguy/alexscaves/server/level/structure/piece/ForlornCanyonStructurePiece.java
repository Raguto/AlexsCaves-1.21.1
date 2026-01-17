package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ForlornCanyonStructurePiece extends AbstractCaveGenerationStructurePiece {

    private static final int SHELL_THICKNESS = 16;

    public ForlornCanyonStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.FORLORN_CANYON.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius);
    }

    public ForlornCanyonStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.FORLORN_CANYON.get(), tag);
    }

    public ForlornCanyonStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        int carvedCount = 0;
        int shellCount = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                MutableBoolean doFloor = new MutableBoolean(false);
                for (int y = 15; y >= 0; y--) {
                    carve.set(cornerX + x, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), cornerZ + z);
                    
                    // First, paint the shell (solid blocks just outside the cave)
                    if (inShell(carve) && !checkedGetBlock(level, carve).is(Blocks.BEDROCK)) {
                        BlockState currentBlock = checkedGetBlock(level, carve);
                        if (!currentBlock.isAir() && isVanillaStone(currentBlock)) {
                            checkedSetBlock(level, carve, ACBlockRegistry.GUANOSTONE.get().defaultBlockState());
                            shellCount++;
                        }
                    }
                    
                    // Then carve the interior
                    if (inCircle(carve) && !checkedGetBlock(level, carve).is(Blocks.BEDROCK)) {
                        checkedSetBlock(level, carve, Blocks.CAVE_AIR.defaultBlockState());
                        surroundCornerOfLiquid(level, carve);
                        carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                        doFloor.setTrue();
                        carvedCount++;
                    } else if (doFloor.isTrue()) {
                        break;
                    }
                }
                if (doFloor.isTrue() && !checkedGetBlock(level, carveBelow).isAir()) {
                    decorateFloor(level, random, carveBelow);
                    doFloor.setFalse();
                }
            }
        }
        
        // Note: biome is already set by voronoi system, no need to call replaceBiomes
    }
    
    private boolean inShell(BlockPos pos) {
        if (inCircle(pos)) {
            return false;
        }
        return inCircleExpanded(pos, SHELL_THICKNESS);
    }
    
    private boolean inCircleExpanded(BlockPos carve, int expansion) {
        float wallNoise = (ACMath.sampleNoise3D(carve.getX(), (int) (carve.getY() * 0.1F), carve.getZ(), 40) + 1.0F) * 0.5F;
        double yDist = ACMath.smin(1F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) ((height + expansion * 2) * 0.5F), 1.0F, 0.3F);
        double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY(), this.holeCenter.getZ());
        double expandedRadius = radius + expansion;
        double targetRadius = yDist * (expandedRadius * wallNoise) * expandedRadius;
        return distToCenter < targetRadius;
    }
    
    private boolean isVanillaStone(BlockState state) {
        return state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE) || 
               state.is(Blocks.GRANITE) || state.is(Blocks.DIORITE) || state.is(Blocks.ANDESITE) ||
               state.is(Blocks.TUFF) || state.is(Blocks.CALCITE) || state.is(Blocks.SMOOTH_BASALT) ||
               state.is(Blocks.DIRT) || state.is(Blocks.GRAVEL) ||
               state.is(Blocks.COBBLED_DEEPSLATE) || state.is(Blocks.INFESTED_STONE) ||
               state.is(Blocks.INFESTED_DEEPSLATE);
    }

    private void surroundCornerOfLiquid(WorldGenLevel level, BlockPos.MutableBlockPos center) {
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            BlockState state = checkedGetBlock(level, offset);
            if (!state.getFluidState().isEmpty()) {
                checkedSetBlock(level, offset, ACBlockRegistry.GUANOSTONE.get().defaultBlockState());
            }
        }
    }

    // Use DinoBowl-style inCircle that works
    private boolean inCircle(BlockPos carve) {
        float wallNoise = (ACMath.sampleNoise3D(carve.getX(), (int) (carve.getY() * 0.1F), carve.getZ(), 40) + 1.0F) * 0.5F;
        double yDist = ACMath.smin(1F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) (height * 0.5F), 1.0F, 0.3F);
        double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY(), this.holeCenter.getZ());
        double targetRadius = yDist * (radius * wallNoise) * radius;
        return distToCenter < targetRadius;
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos.MutableBlockPos carveBelow) {
        float floorNoise = (ACMath.sampleNoise2D(carveBelow.getX(), carveBelow.getZ(), 50) + 1.0F) * 0.5F;
        checkedSetBlock(level, carveBelow, Blocks.PACKED_MUD.defaultBlockState());
        for (int i = 0; i < Math.ceil(floorNoise * 3); i++) {
            carveBelow.move(0, 1, 0);
            checkedSetBlock(level, carveBelow, Blocks.PACKED_MUD.defaultBlockState());
        }
    }
}
