package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACLootTableRegistry {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_DEF_REG = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlexsCaves.MODID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_DEF_REG = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, AlexsCaves.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<CaveTabletLootModifier>> CAVE_TABLET_LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_DEF_REG.register("cave_tablet", () -> CaveTabletLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<CabinMapLootModifier>> CABIN_MAP_LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_DEF_REG.register("cabin_map", () -> CabinMapLootModifier.CODEC);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GummyColorLootFunction>> GUMMY_COLORS_LOOT_FUNCTION = LOOT_FUNCTION_DEF_REG.register("gummy_colors", () -> new LootItemFunctionType<>(GummyColorLootFunction.CODEC));

    public static final ResourceLocation ABYSSAL_RUINS_CHEST = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/abyssal_ruins");
    public static final ResourceLocation WITCH_HUT_CHEST = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/witch_hut");
    public static final ResourceLocation LICOWITCH_TOWER_CHEST = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower");
    public static final ResourceLocation SECRET_LICOWITCH_TOWER_CHEST = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower_secret");
    public static final ResourceLocation GINGERBREAD_TOWN_CHEST = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/gingerbread_town");

}
