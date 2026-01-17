package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ForlornBridgeStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ForlornBridgeStructure extends Structure {

    public static int BRIDGE_SECTION_LENGTH = 6;
    public static int BRIDGE_SECTION_WIDTH = 4;

    public static final MapCodec<ForlornBridgeStructure> CODEC = simpleCodec((settings) -> new ForlornBridgeStructure(settings));

    protected ForlornBridgeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureStart generate(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, 
                                   RandomState randomState, StructureTemplateManager templateManager, long seed, 
                                   ChunkPos chunkPos, int references, LevelHeightAccessor heightAccessor, 
                                   Predicate<Holder<Biome>> validBiome) {
        
        Structure.GenerationContext context = new Structure.GenerationContext(registryAccess, chunkGenerator, biomeSource, 
            randomState, templateManager, seed, chunkPos, heightAccessor, validBiome);
        
        Optional<Structure.GenerationStub> optional = this.findGenerationPoint(context);
        
        if (optional.isPresent()) {
            Structure.GenerationStub stub = optional.get();
            StructurePiecesBuilder builder = stub.getPiecesBuilder();
            StructureStart structureStart = new StructureStart(this, chunkPos, references, builder.build());
            
            if (structureStart.isValid()) {
                return structureStart;
            }
        }
        
        return StructureStart.INVALID_START;
    }

    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int x = chunkpos.getMiddleBlockX();
        int z = chunkpos.getMiddleBlockZ();
        
        long seed = context.seed();
        ResourceKey<Biome> biomeAtLocation = ACBiomeRarity.getACBiomeForPosition(seed, x, z);
        
        if (biomeAtLocation == null || !biomeAtLocation.equals(ACBiomeRegistry.FORLORN_HOLLOWS)) {
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
        int k = ForlornCanyonStructure.BOWL_Y_CENTER;
        return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    public void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos xzCoords = new BlockPos(i, ForlornCanyonStructure.BOWL_Y_CENTER, j);
        
        long seed = context.seed();
        int biomeUp = biomeContinuesInDirectionForVoronoi(seed, Direction.UP, xzCoords, 32);
        int biomeDown = biomeContinuesInDirectionForVoronoi(seed, Direction.DOWN, xzCoords, 32);
        BlockPos center = xzCoords.below(biomeDown).above(worldgenrandom.nextInt(Math.max(biomeUp, 10)));
        Direction bridgeDirection = Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, worldgenrandom);
        int biomeForwards = biomeContinuesInDirectionForVoronoi(seed, bridgeDirection, center, 32 + worldgenrandom.nextInt(6) * 16);
        int biomeBackwards = biomeContinuesInDirectionForVoronoi(seed, bridgeDirection.getOpposite(), center, 32 + worldgenrandom.nextInt(6) * 16);
        int maxSections = (int) Math.ceil((biomeBackwards + biomeForwards) / BRIDGE_SECTION_LENGTH);
        for (int section = 0; section <= maxSections; section++) {
            BlockPos at = center.relative(bridgeDirection, section * BRIDGE_SECTION_LENGTH - BRIDGE_SECTION_LENGTH / 2 - biomeBackwards);
            builder.addPiece(new ForlornBridgeStructurePiece(at, section, maxSections, bridgeDirection));
        }
    }

    protected int biomeContinuesInDirectionForVoronoi(long seed, Direction direction, BlockPos start, int cutoff) {
        int i = 0;
        while (i < cutoff) {
            BlockPos check = start.relative(direction, i);
            ResourceKey<Biome> biomeAtPos = ACBiomeRarity.getACBiomeForPosition(seed, check.getX(), check.getZ());
            if (biomeAtPos == null || !biomeAtPos.equals(ACBiomeRegistry.FORLORN_HOLLOWS)) {
                break;
            }
            i += 16;
        }
        return Math.min(i, cutoff);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.FORLORN_BRIDGE.get();
    }
}