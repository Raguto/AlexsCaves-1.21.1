package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CaveTabletLootModifier extends LootModifier {

    private static final MapCodec<ResourceKey<Biome>> ENTRY_CODEC = ResourceKey.codec(Registries.BIOME).fieldOf("biome");

    public static final MapCodec<CaveTabletLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst).and(
                            inst.group(
                                    ENTRY_CODEC.forGetter((configuration) -> configuration.biome),
                                    Codec.BOOL.fieldOf("replace").forGetter((configuration) -> configuration.replace)
                            )
                    ).apply(inst, CaveTabletLootModifier::new));

    private final ResourceKey<Biome> biome;
    private final boolean replace;

    protected CaveTabletLootModifier(LootItemCondition[] conditionsIn, ResourceKey<Biome> biome, boolean replace) {
        super(conditionsIn);
        this.biome = biome;
        this.replace = replace;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < getChance()) {
            if (replace) {
                generatedLoot.clear();
            }
            generatedLoot.add(getTablet());
        }
        return generatedLoot;
    }

    private float getChance() {
        if (biome == null || BiomeGenerationConfig.isBiomeDisabledCompletely(biome)) {
            return 0F;
        }
        if (biome.equals(ACBiomeRegistry.MAGNETIC_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.magneticTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.PRIMORDIAL_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.primordialTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.TOXIC_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.toxicTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.ABYSSAL_CHASM)) {
            return AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.FORLORN_HOLLOWS)) {
            return AlexsCaves.COMMON_CONFIG.forlornTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.CANDY_CAVITY)) {
            return AlexsCaves.COMMON_CONFIG.candyTabletLootChance.get().floatValue();
        }
        return 0F;
    }

    private ItemStack getTablet() {
        CompoundTag tag = new CompoundTag();
        ResourceKey<Biome> key = ACBiomeRegistry.MAGNETIC_CAVES;
        if (biome != null) {
            key = biome;
        }
        tag.putString("CaveBiome", key.location().toString());
        ItemStack stack = new ItemStack(ACItemRegistry.CAVE_TABLET.get());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
