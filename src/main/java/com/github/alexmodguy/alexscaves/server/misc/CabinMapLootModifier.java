package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class CabinMapLootModifier extends LootModifier {
    public static final MapCodec<CabinMapLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst).apply(inst, CabinMapLootModifier::new));

    protected CabinMapLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < getChance() && context.hasParam(LootContextParams.ORIGIN)) {
            ServerLevel serverlevel = context.getLevel();
            BlockPos chestPos = BlockPos.containing(context.getParam(LootContextParams.ORIGIN));
            BlockPos blockpos = serverlevel.findNearestMapStructure(ACTagRegistry.ON_UNDERGROUND_CABIN_MAPS, chestPos, 100, true);
            if(blockpos != null){
                ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverlevel, itemstack);
                // In 1.21, use MapDecorationTypes.TARGET_POINT as a fallback since custom map decorations require registry
                MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", MapDecorationTypes.TARGET_POINT);
                itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.alexscaves.underground_cabin_explorer_map"));
                generatedLoot.add(itemstack);
            }

        }
        return generatedLoot;
    }

    private float getChance() {
        return AlexsCaves.COMMON_CONFIG.cabinMapLootChance.get().floatValue();
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
