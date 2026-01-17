package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ForlornCanyonStructurePiece;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ForlornCanyonStructure extends AbstractCaveGenerationStructure {

    private static final int BOWL_WIDTH_RADIUS = 100;
    private static final int BOWL_HEIGHT_RADIUS = 60;
    public static final int BOWL_Y_CENTER = -10;

    public static final MapCodec<ForlornCanyonStructure> CODEC = simpleCodec((settings) -> new ForlornCanyonStructure(settings));

    public ForlornCanyonStructure(StructureSettings settings) {
        super(settings, ACBiomeRegistry.FORLORN_HOLLOWS);
    }

    /**
     * Override generate to bypass Minecraft's biome validation for Forlorn Hollows.
     * The voronoi system already ensures we're in the right biome area.
     */
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

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int x = chunkpos.getMiddleBlockX();
        int z = chunkpos.getMiddleBlockZ();
        
        long seed = context.seed();
        ResourceKey<Biome> biomeAtLocation = ACBiomeRarity.getACBiomeForPosition(seed, x, z);
        
        if (biomeAtLocation == null || !biomeAtLocation.equals(ACBiomeRegistry.FORLORN_HOLLOWS)) {
            return Optional.empty();
        }

        net.minecraft.world.phys.Vec3 biomeCenter = ACBiomeRarity.getACBiomeCenterForPosition(seed, x, z);
        if (biomeCenter == null) {
            return Optional.empty();
        }
        
        int centerBlockX = (int) biomeCenter.x * 4;
        int centerBlockZ = (int) biomeCenter.z * 4;
        int centerChunkX = centerBlockX >> 4;
        int centerChunkZ = centerBlockZ >> 4;
        
        if (Math.abs(chunkpos.x - centerChunkX) > 3 || Math.abs(chunkpos.z - centerChunkZ) > 3) {
            return Optional.empty();
        }
        
        int validY = findValidBiomeY(context, x, z);
        
        return Optional.of(new Structure.GenerationStub(new BlockPos(x, validY, z), (builder) -> {
            this.generatePieces(builder, context);
        }));
    }
    
    private int findValidBiomeY(Structure.GenerationContext context, int blockX, int blockZ) {
        int[] yLevelsToCheck = {-20, -30, -40, -50, -60, 0, 10, -10, -70, -80};
        
        for (int y : yLevelsToCheck) {
            Holder<Biome> biomeAtY = context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(blockX), 
                QuartPos.fromBlock(y), 
                QuartPos.fromBlock(blockZ), 
                context.randomState().sampler()
            );
            if (biomeAtY.is(ACBiomeRegistry.FORLORN_HOLLOWS)) {
                return y;
            }
        }
        
        return BOWL_Y_CENTER;
    }

    @Override
    protected StructurePiece createPiece(BlockPos offset, BlockPos center, int heightBlocks, int widthBlocks, RandomState randomState) {
        return new ForlornCanyonStructurePiece(offset, center, heightBlocks, widthBlocks);
    }

    @Override
    public int getGenerateYHeight(WorldgenRandom random, int x, int y) {
        return BOWL_Y_CENTER;
    }

    @Override
    public int getWidthRadius(WorldgenRandom random) {
        return BOWL_WIDTH_RADIUS;
    }

    @Override
    public int getHeightRadius(WorldgenRandom random, int seaLevel) {
        return 90;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.FORLORN_CANYON.get();
    }
}

