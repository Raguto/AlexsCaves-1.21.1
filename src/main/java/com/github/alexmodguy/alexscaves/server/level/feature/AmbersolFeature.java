package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.AmbersolBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AmbersolFeature extends Feature<NoneFeatureConfiguration> {

    public AmbersolFeature(Codec<NoneFeatureConfiguration> config) {
        super(config);
    }

    public boolean place(@Nonnull FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos blockpos = context.origin();
        WorldGenLevel worldgenlevel = context.level();
        RandomSource randomsource = context.random();

        int maxY = worldgenlevel.getMaxBuildHeight() - 3;
        BlockPos.MutableBlockPos cursor = context.origin().mutable();
        while (cursor.getY() < maxY && worldgenlevel.isEmptyBlock(cursor)) {
            BlockPos aboveCursor = Objects.requireNonNull(cursor.above(), "aboveCursor");
            if (!worldgenlevel.isEmptyBlock(aboveCursor)) {
                break;
            }
            cursor.move(0, 1, 0);
        }
        blockpos = cursor.immutable();
        if (blockpos.getY() >= maxY) {
            return false;
        }
        if (!worldgenlevel.isEmptyBlock(blockpos)) {
            return false;
        }
        BlockPos ceilingPos = Objects.requireNonNull(blockpos.above(), "ceilingPos");
        if (!worldgenlevel.getBlockState(ceilingPos).isFaceSturdy(worldgenlevel, ceilingPos, Direction.DOWN)) {
            return false;
        } else {
            BlockState amberState = Objects.requireNonNull(ACBlockRegistry.AMBER.get().defaultBlockState(), "amberState");
            for (int i = 0; i < 3; i++) {
                drawOrb(worldgenlevel, blockpos.offset(randomsource.nextInt(4) - 2, randomsource.nextInt(4) - 2, randomsource.nextInt(4) - 2), randomsource, amberState, 2 + randomsource.nextInt(2), 2 + randomsource.nextInt(2), 2 + randomsource.nextInt(2));
            }
            drawOrb(worldgenlevel, blockpos, randomsource, amberState, 2, 2, 2);
            worldgenlevel.setBlock(blockpos, Objects.requireNonNull(ACBlockRegistry.AMBERSOL.get().defaultBlockState(), "ambersolState"), 3);
            AmbersolBlock.fillWithLights(blockpos, worldgenlevel);
            return true;
        }
    }

    private static boolean canReplace(@Nonnull BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    private static void drawOrb(WorldGenLevel level, BlockPos center, RandomSource random, @Nonnull BlockState blockState, int radiusX, int radiusY, int radiusZ) {
        double equalRadius = (radiusX + radiusY + radiusZ) / 3.0D;
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    BlockPos fill = center.offset(x, y, z);
                    if (fill.distToLowCornerSqr(center.getX(), center.getY(), center.getZ()) <= equalRadius * equalRadius - random.nextFloat() * 4) {
                        if (canReplace(Objects.requireNonNull(level.getBlockState(fill), "fillState"))) {
                            level.setBlock(fill, Objects.requireNonNull(blockState, "blockState"), 2);
                        }
                    }
                }
            }
        }
    }
}
