package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveInfoItem;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class RecipeCaveMap extends CustomRecipe implements SpecialRecipeInGuideBook {
    public RecipeCaveMap(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) {
            return false;
        }
        boolean hasCodex = false;
        int paperCount = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(ACItemRegistry.CAVE_CODEX.get())) {
                if (hasCodex) return false; // Only one codex allowed
                hasCodex = true;
            } else if (stack.is(Items.PAPER)) {
                paperCount++;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return hasCodex && paperCount == 8;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); ++i) {
            if (!input.getItem(i).isEmpty() && input.getItem(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = input.getItem(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }

    public RecipeSerializer<?> getSerializer() {
        return ACRecipeRegistry.CAVE_MAP.get();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        return NonNullList.of(Ingredient.EMPTY, 
            Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), 
            Ingredient.of(Items.PAPER), Ingredient.of(ACItemRegistry.CAVE_CODEX.get()), Ingredient.of(Items.PAPER), 
            Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER));
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i < nonNullList.size(); ++i) {
            if (!nonNullList.get(i).isEmpty() && nonNullList.get(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = nonNullList.get(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }
}

