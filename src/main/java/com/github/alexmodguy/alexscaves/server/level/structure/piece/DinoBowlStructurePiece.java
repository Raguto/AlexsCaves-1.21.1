package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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

public class DinoBowlStructurePiece extends AbstractCaveGenerationStructurePiece {

    public DinoBowlStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius);
    }

    public DinoBowlStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.DINO_BOWL.get(), tag);
    }

    public DinoBowlStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerY = this.chunkCorner.getY();
        int cornerZ = this.chunkCorner.getZ();
        
        // Get the bounds of the chunk being processed to avoid far chunk errors
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMaxX = chunkPos.getMaxBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxZ = chunkPos.getMaxBlockZ();
        
        boolean flag = false;
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(cornerX, cornerY, cornerZ);
        carveBelow.set(cornerX, cornerY, cornerZ);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = cornerX + x;
                int worldZ = cornerZ + z;
                
                // Skip if outside the chunk being processed
                if (worldX < chunkMinX || worldX > chunkMaxX || worldZ < chunkMinZ || worldZ > chunkMaxZ) {
                    continue;
                }
                
                MutableBoolean doFloor = new MutableBoolean(false);
                for (int y = 15; y >= 0; y--) {
                    carve.set(worldX, Mth.clamp(cornerY + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), worldZ);
                    if (inCircle(carve) && !checkedGetBlock(level, carve).is(Blocks.BEDROCK)) {
                        flag = true;
                        checkedSetBlock(level, carve, Blocks.CAVE_AIR.defaultBlockState());
                        surroundCornerOfLiquid(level, carve, chunkPos);
                        carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                        doFloor.setTrue();
                    }
                }
                if (doFloor.isTrue()) {
                    BlockState floor = checkedGetBlock(level, carveBelow);
                    if (!floor.isAir() && !floor.is(ACTagRegistry.VOLCANO_BLOCKS)) {
                        decorateFloor(level, random, carveBelow.immutable(), chunkPos);
                    }
                    doFloor.setFalse();
                }
            }
        }
        if (flag) {
            replaceBiomes(level, ACBiomeRegistry.PRIMORDIAL_CAVES, 32);
        }
    }

    /**
     * Surrounds liquid blocks with sandstone, but only within the current chunk to avoid far chunk errors.
     */
    private void surroundCornerOfLiquid(WorldGenLevel level, Vec3i center, ChunkPos chunkPos) {
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMaxX = chunkPos.getMaxBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxZ = chunkPos.getMaxBlockZ();
        
        BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.values()) {
            offset.set(center);
            offset.move(dir);
            // Only modify blocks within the current chunk
            if (offset.getX() >= chunkMinX && offset.getX() <= chunkMaxX &&
                offset.getZ() >= chunkMinZ && offset.getZ() <= chunkMaxZ) {
                BlockState state = checkedGetBlock(level, offset);
                if (!state.getFluidState().isEmpty()) {
                    checkedSetBlock(level, offset, Blocks.SANDSTONE.defaultBlockState());
                }
            }
        }
    }

    /**
     * Determines if a position should be carved out based on distance from center and noise.
     */
    private boolean inCircle(BlockPos carve) {
        float wallNoise = (ACMath.sampleNoise3D(carve.getX(), (int) (carve.getY() * 0.1F), carve.getZ(), 40) + 1.0F) * 0.5F;
        double yDist = ACMath.smin(1F - Math.abs(this.holeCenter.getY() - carve.getY()) / (float) (height * 0.5F), 1.0F, 0.3F);
        double distToCenter = carve.distToLowCornerSqr(this.holeCenter.getX(), carve.getY(), this.holeCenter.getZ());
        double targetRadius = yDist * (radius * wallNoise) * radius;
        return distToCenter < targetRadius;
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos carveBelow, ChunkPos chunkPos) {
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMaxX = chunkPos.getMaxBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkMaxZ = chunkPos.getMaxBlockZ();
        
        // Only decorate if within current chunk
        if (carveBelow.getX() < chunkMinX || carveBelow.getX() > chunkMaxX ||
            carveBelow.getZ() < chunkMinZ || carveBelow.getZ() > chunkMaxZ) {
            return;
        }
        
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        checkedSetBlock(level, carveBelow, grass);
        for (int i = 0; i < 1 + rand.nextInt(2); i++) {
            carveBelow = carveBelow.below();
            checkedSetBlock(level, carveBelow, dirt);
        }
    }
}
