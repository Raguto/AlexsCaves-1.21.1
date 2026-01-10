package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.VolcanoStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class VolcanoStructure extends Structure {

    public static final int VOLCANO_Y_CENTER = -34;

    public static final MapCodec<VolcanoStructure> CODEC = simpleCodec((settings) -> new VolcanoStructure(settings));

    protected VolcanoStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int x = chunkpos.getMiddleBlockX();
        int z = chunkpos.getMiddleBlockZ();
        
        // Use ACBiomeRarity directly to check if this location is in the correct AC biome region
        // This bypasses the biome source which has Y-level and timing issues
        long seed = context.seed();
        ResourceKey<Biome> biomeAtLocation = com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity.getACBiomeForPosition(seed, x, z);
        
        boolean biomeMatches = biomeAtLocation != null && biomeAtLocation.equals(ACBiomeRegistry.PRIMORDIAL_CAVES);
        
        if (!biomeMatches) {
            return Optional.empty();
        }

        return atYCaveBiomePoint(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            this.generatePieces(builder, context);
        });
    }

    protected Optional<GenerationStub> atYCaveBiomePoint(GenerationContext context, Heightmap.Types heightMap, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        int k = DinoBowlStructure.BOWL_Y_CENTER;
        return Optional.of(new GenerationStub(new BlockPos(i, k, j), builderConsumer));
    }

    public void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        int volcanoWidthRadius = 30 + context.random().nextInt(15);
        int volcanoHeight = volcanoWidthRadius + 8 + context.random().nextInt(5);
        int i = context.chunkPos().getMinBlockX();
        int j = context.chunkPos().getMinBlockZ();
        int k = context.chunkGenerator().getSeaLevel();
        BlockPos xzCoords = new BlockPos(i, VOLCANO_Y_CENTER, j);
        int widthChunks = (int)Math.ceil(volcanoWidthRadius / 16F);
        for (int chunkX = -widthChunks; chunkX <= widthChunks; chunkX++) {
            for (int chunkZ = -widthChunks; chunkZ <= widthChunks; chunkZ++) {
                BlockPos offset = xzCoords.offset(chunkX * 16, 0, chunkZ * 16);
                builder.addPiece(new VolcanoStructurePiece(xzCoords, offset, volcanoWidthRadius, volcanoHeight));
            }
        }
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.VOLCANO.get();
    }
}
