package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class SulfurBlock extends Block {

    public SulfurBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2F, 4.0F).sound(ACSoundTypes.SULFUR).randomTicks());
    }

    public void randomTick(BlockState currentState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(10) == 0) {
            Direction direction = Util.getRandom(Direction.values(), randomSource);
            BlockPos offset = blockPos.relative(direction);
            if (level.getBlockState(offset).isAir() && isDrippingAcidAbove(level, offset)) {
                BlockState blockstate1 = ACBlockRegistry.SULFUR_BUD_SMALL.get().defaultBlockState().setValue(SulfurBudBlock.FACING, direction).setValue(SulfurBudBlock.LIQUID_LOGGED, SulfurBudBlock.getLiquidType(level.getFluidState(offset)));
                level.setBlockAndUpdate(offset, blockstate1);
            }
        }
    }

    private boolean isDrippingAcidAbove(Level level, BlockPos pos) {
        if (level.getFluidState(pos).getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get()) {
            return true;
        }
        while (level.getBlockState(pos).isAir() && pos.getY() < level.getMaxBuildHeight()) {
            pos = pos.above();
        }
        BlockState acidState = level.getBlockState(pos);
        return acidState.is(ACBlockRegistry.ACIDIC_RADROCK.get());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        Holder<Enchantment> silkTouch = builder.getLevel().registryAccess()
            .registryOrThrow(Registries.ENCHANTMENT)
            .getHolderOrThrow(Enchantments.SILK_TOUCH);
        if (tool != null && EnchantmentHelper.getItemEnchantmentLevel(silkTouch, tool) > 0) {
            return List.of(new ItemStack(this));
        }

        List<ItemStack> drops = super.getDrops(state, builder);
        ItemStack dust = new ItemStack(ACItemRegistry.SULFUR_DUST.get());
        boolean hasDust = drops.stream().anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, dust));
        if (hasDust) {
            return drops;
        }

        RandomSource random = builder.getLevel().getRandom();
        int count = 1 + random.nextInt(3);
        if (tool != null) {
            Holder<Enchantment> fortune = builder.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FORTUNE);
            int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(fortune, tool);
            if (fortuneLevel > 0) {
                count += random.nextInt(fortuneLevel + 1);
            }
        }
        count = Math.min(5, Math.max(1, count));
        dust.setCount(count);
        return List.of(dust);
    }
}
