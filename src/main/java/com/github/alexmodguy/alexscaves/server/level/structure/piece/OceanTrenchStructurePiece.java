package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class OceanTrenchStructurePiece extends AbstractCaveGenerationStructurePiece {

    private BlockState water = Fluids.WATER.defaultFluidState().createLegacyBlock();

    public OceanTrenchStructurePiece(BlockPos chunkCorner, BlockPos holeCenter, int bowlHeight, int bowlRadius) {
        super(ACStructurePieceRegistry.OCEAN_TRENCH.get(), chunkCorner, holeCenter, bowlHeight, bowlRadius, -64, 100);
    }

    public OceanTrenchStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.OCEAN_TRENCH.get(), tag);
    }

    public OceanTrenchStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int cornerX = this.chunkCorner.getX();
        int cornerZ = this.chunkCorner.getZ();
        int seaLevel = chunkGen.getSeaLevel();
        boolean flag = false;
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int carveX = cornerX + x;
                int carveZ = cornerZ + z;
                
                // Check if this XZ position is within the trench radius
                double distToCenter = Math.sqrt(carve.set(carveX, 0, carveZ).distToLowCornerSqr(this.holeCenter.getX(), 0, this.holeCenter.getZ()));
                double trenchRadius = getTrenchRadius(carveX, carveZ);
                
                if (distToCenter > trenchRadius) {
                    continue; // Outside trench, skip
                }
                
                // Calculate how deep to carve based on distance from center
                double edgeFactor = 1.0 - (distToCenter / trenchRadius);
                edgeFactor = Math.pow(edgeFactor, 0.5); // Smoother falloff
                
                int priorHeight = getSeafloorHeight(level, carveX, carveZ);
                float floorNoise = (1.0F + ACMath.sampleNoise2D(carveX - 800, carveZ - 212, 20)) * 5;
                int trenchBottom = (int) (level.getMinBuildHeight() + 5 + floorNoise);
                int carveDepth = (int) ((priorHeight - trenchBottom) * edgeFactor);
                int targetFloor = priorHeight - carveDepth;
                
                // Skip if seafloor is above sea level (not in ocean)
                if (priorHeight >= seaLevel - 3) {
                    continue;
                }
                
                MutableBoolean didCarve = new MutableBoolean(false);
                
                // Carve from seafloor down to target floor
                for (int y = priorHeight; y >= targetFloor; y--) {
                    carve.set(carveX, y, carveZ);
                    BlockState prior = checkedGetBlock(level, carve);
                    
                    if (prior.is(Blocks.BEDROCK)) continue;
                    
                    if (isSeaMountBlocking(carve)) {
                        // Create sea mount pillar
                        if (!prior.is(Blocks.BEDROCK)) {
                            checkedSetBlock(level, carve, Blocks.TUFF.defaultBlockState());
                        }
                    } else {
                        // Carve with water
                        flag = true;
                        didCarve.setTrue();
                        checkedSetBlock(level, carve, water);
                    }
                }
                
                // Decorate the floor
                if (didCarve.isTrue() && targetFloor > level.getMinBuildHeight() + 2) {
                    carveBelow.set(carveX, targetFloor - 1, carveZ);
                    decorateFloor(level, random, carveBelow, seaLevel);
                    
                    // Build walls on edges
                    if (edgeFactor < 0.3) {
                        buildWall(level, carveX, carveZ, targetFloor, priorHeight);
                    }
                }
            }
        }
        if (flag) {
            replaceBiomes(level, ACBiomeRegistry.ABYSSAL_CHASM, 16);
        }
    }
    
    private double getTrenchRadius(int x, int z) {
        float simplex1 = ACMath.sampleNoise2D(x, z, 50);
        float simplex2 = ACMath.sampleNoise2D(x + 1000, z - 1000, 120);
        float widthNoise = 0.85F + 0.15F * (1F + simplex1 + simplex2) * 0.5F;
        return radius * widthNoise;
    }
    
    private void buildWall(WorldGenLevel level, int x, int z, int bottom, int top) {
        BlockPos.MutableBlockPos wallPos = new BlockPos.MutableBlockPos();
        for (int y = bottom; y <= top; y++) {
            wallPos.set(x, y, z);
            BlockState prior = checkedGetBlock(level, wallPos);
            if (!prior.is(Blocks.BEDROCK) && (prior.isAir() || prior.getFluidState().is(Fluids.WATER))) {
                int dist = top - y;
                BlockState toSet;
                if (dist <= 5) {
                    toSet = y < 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
                } else if (dist <= 12) {
                    toSet = Blocks.DEEPSLATE.defaultBlockState();
                } else {
                    toSet = ACBlockRegistry.ABYSSMARINE.get().defaultBlockState();
                }
                checkedSetBlock(level, wallPos, toSet);
            }
        }
    }

    private int getSeafloorHeight(WorldGenLevel level, int x, int z) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), z);
        int yPrev = mutableBlockPos.getY();
        //check surface
        mutableBlockPos.setY(level.getSeaLevel() + 5);
        boolean inFrozenOcean = level.getBiome(mutableBlockPos).is(ACTagRegistry.TRENCH_IGNORES_STONE_IN);
        mutableBlockPos.setY(yPrev);
        while (ignoreHeight(level, inFrozenOcean, checkedGetBlock(level, mutableBlockPos), mutableBlockPos) && mutableBlockPos.getY() >= -64) {
            mutableBlockPos.move(0, -1, 0);
        }
        return mutableBlockPos.getY();
    }

    private boolean ignoreHeight(WorldGenLevel level, boolean inFrozenOcean, BlockState blockState, BlockPos.MutableBlockPos mutableBlockPos) {
        return blockState.isAir() || blockState.is(ACTagRegistry.TRENCH_GENERATION_IGNORES) || !blockState.getFluidState().isEmpty() || inFrozenOcean && (blockState.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES) && mutableBlockPos.getY() > level.getSeaLevel() - 5);
    }

    private boolean isSeaMountBlocking(BlockPos carve) {
        int bottomedY = carve.getY() + 64;
        float heightTarget = 20 + ACMath.sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 30) * 10 + ACMath.sampleNoise3D(carve.getX() - 110, 0, carve.getZ() + 110, 10) * 3;
        float heightScale = (heightTarget - bottomedY) / (heightTarget + 15);
        float sample = ACMath.sampleNoise3D(carve.getX(), 0, carve.getZ(), 50) + ACMath.sampleNoise3D(carve.getX() - 440, 0, carve.getZ() + 412, 11) * 0.2F + ACMath.sampleNoise3D(carve.getX() - 100, 0, carve.getZ() - 400, 100) * 0.3F - 0.1F;
        return sample >= 0.4F * Math.max(0, 1 - heightScale);
    }

    private void decorateFloor(WorldGenLevel level, RandomSource rand, BlockPos.MutableBlockPos muckAt, int seaLevel) {
        if (!isSeaMountBlocking(muckAt) && muckAt.getY() < seaLevel - 10) {
            checkedSetBlock(level, muckAt, ACBlockRegistry.MUCK.get().defaultBlockState());
            for (int i = 0; i < 1 + rand.nextInt(2); i++) {
                muckAt.move(0, -1, 0);
                BlockState at = checkedGetBlock(level, muckAt);
                if (at.is(ACTagRegistry.UNMOVEABLE) || at.is(ACTagRegistry.TRENCH_GENERATION_IGNORES)) {
                    break;
                }
                if (!at.getFluidState().isEmpty() && !at.getFluidState().is(FluidTags.WATER)) {
                    checkedSetBlock(level, muckAt, Blocks.DEEPSLATE.defaultBlockState());
                } else {
                    checkedSetBlock(level, muckAt, ACBlockRegistry.MUCK.get().defaultBlockState());
                }
            }
        }
    }
}
