package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface UpdatesStackTags {

    default void updateTagFromServer(Entity holder, ItemStack stack, CompoundTag tag){
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
