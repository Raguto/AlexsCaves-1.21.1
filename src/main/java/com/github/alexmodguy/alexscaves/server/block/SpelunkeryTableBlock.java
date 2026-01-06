package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.inventory.SpelunkeryTableMenu;
import com.github.alexmodguy.alexscaves.server.message.SpelunkeryTableCompleteTutorialMessage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class SpelunkeryTableBlock extends Block {
    public static final MapCodec<SpelunkeryTableBlock> CODEC = simpleCodec(props -> new SpelunkeryTableBlock());
    private static final Component CONTAINER_TITLE = Component.translatable("alexscaves.container.spelunkery_table");

    public SpelunkeryTableBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD));
    }

    @Override
    public MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_LOOM);
            if (player instanceof ServerPlayer serverPlayer) {
                AlexsCaves.sendNonLocal(new SpelunkeryTableCompleteTutorialMessage(SpelunkeryTableMenu.hasCompletedTutorial(serverPlayer)), serverPlayer);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((i, inv, player) -> {
            return new SpelunkeryTableMenu(i, inv, ContainerLevelAccess.create(level, pos));
        }, CONTAINER_TITLE);
    }
}
