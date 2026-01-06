package com.github.alexmodguy.alexscaves.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CaveTabletSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final CaveTabletSubtypeInterpreter INSTANCE = new CaveTabletSubtypeInterpreter();

    private CaveTabletSubtypeInterpreter() {

    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if(customData != null){
            CompoundTag tag = customData.copyTag();
            if(tag.contains("CaveBiome")){
                return tag.getString("CaveBiome");
            }
        }
        return "";
    }
}
