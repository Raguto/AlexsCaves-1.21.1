package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractCaveGenerationStructure extends Structure {

    private final ResourceKey<Biome> matchingBiome;

    protected AbstractCaveGenerationStructure(StructureSettings settings, ResourceKey<Biome> matchingBiome) {
        super(settings);
        this.matchingBiome = matchingBiome;
    }

    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int x = chunkpos.getMiddleBlockX();
        int z = chunkpos.getMiddleBlockZ();
        
        // Use voronoi to check if this location should have this biome type
        long seed = context.seed();
        ResourceKey<Biome> biomeAtLocation = ACBiomeRarity.getACBiomeForPosition(seed, x, z);
        
        if (biomeAtLocation == null || !biomeAtLocation.equals(matchingBiome)) {
            return Optional.empty();
        }
        
        return atYCaveBiomePoint(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    protected Optional<Structure.GenerationStub> atYCaveBiomePoint(Structure.GenerationContext context, Heightmap.Types heightMap, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        int k = getGenerateYHeight(context.random(), i, j);
        return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    public void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos center = new BlockPos(i, getGenerateYHeight(context.random(), i, j), j);
        int heightRad = getHeightRadius(context.random(), context.chunkGenerator().getSeaLevel());
        int widthRad = getWidthRadius(context.random());
        
        // Use voronoi to check how far the biome extends
        long seed = context.seed();
        int biomeUp = biomeContinuesInDirectionForVoronoi(seed, Direction.UP, center, heightRad) + getYExpandUp();
        int biomeDown = biomeContinuesInDirectionForVoronoi(seed, Direction.DOWN, center, heightRad) + getYExpandDown();
        BlockPos ground = center.below(biomeDown - 2);
        int biomeEast = biomeContinuesInDirectionForVoronoi(seed, Direction.EAST, ground, widthRad);
        int biomeWest = biomeContinuesInDirectionForVoronoi(seed, Direction.WEST, ground, widthRad);
        int biomeNorth = biomeContinuesInDirectionForVoronoi(seed, Direction.NORTH, ground, widthRad);
        int biomeSouth = biomeContinuesInDirectionForVoronoi(seed, Direction.SOUTH, ground, widthRad);
        int widthBlocks = (biomeEast + biomeWest + biomeNorth + biomeSouth) / 4;
        int heightBlocks = (biomeUp + biomeDown) / 2;
        
        int widthChunks = (int) Math.ceil((widthBlocks + 16) / 16F / 2F) + 2;
        int heightChunks = getHeightOverride((int) Math.ceil((heightBlocks + 16) / 16F / 2F));
        int minYChunks = (int) Math.ceil(context.chunkGenerator().getMinY() / 16F);
        int maxYChunks = (int) Math.ceil(320 / 16F);
        
        for (int chunkX = -widthChunks; chunkX <= widthChunks; chunkX++) {
            for (int chunkZ = -widthChunks; chunkZ <= widthChunks; chunkZ++) {
                for (int chunkY = Math.max(-heightChunks, minYChunks); chunkY <= Math.min(heightChunks, maxYChunks); chunkY++) {
                    StructurePiece piece = createPiece(center.offset(new BlockPos(chunkX * 16, chunkY * 16, chunkZ * 16)), center, heightBlocks, widthBlocks, context.randomState());
                    builder.addPiece(piece);
                }
            }
        }
    }
    
    /**
     * Check how far the matching biome extends in a direction using ACBiomeRarity voronoi system.
     */
    protected int biomeContinuesInDirectionForVoronoi(long seed, Direction direction, BlockPos start, int cutoff) {
        int i = 0;
        while (i < cutoff) {
            BlockPos check = start.relative(direction, i);
            ResourceKey<Biome> biomeAtPos = ACBiomeRarity.getACBiomeForPosition(seed, check.getX(), check.getZ());
            if (biomeAtPos == null || !biomeAtPos.equals(matchingBiome)) {
                break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }
    
    protected int getHeightOverride(int heightIn) {
        return heightIn;
    }

    protected int getYExpandUp() {
        return -5;
    }

    protected int getYExpandDown() {
        return -5;
    }

    protected abstract StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks, RandomState randomState);

    private static Holder<Biome> getBiomeHolder(BiomeSource biomeSource, RandomState randomState, BlockPos pos) {
        return biomeSource.getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), randomState.sampler());
    }

    protected int biomeContinuesInDirectionFor(BiomeSource biomeSource, RandomState randomState, Direction direction, BlockPos start, int cutoff) {
        int i = 0;
        while (i < cutoff) {
            BlockPos check = start.relative(direction, i);
            Holder<Biome> biomeHolder = getBiomeHolder(biomeSource, randomState, check);
            if (!biomeHolder.is(matchingBiome)) {
                break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }

    public abstract int getGenerateYHeight(WorldgenRandom random, int x, int y);

    public abstract int getWidthRadius(WorldgenRandom random);

    public abstract int getHeightRadius(WorldgenRandom random, int seaLevel);

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.RAW_GENERATION;
    }
}
