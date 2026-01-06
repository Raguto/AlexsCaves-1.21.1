package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    
    @Accessor("rootAdvancement")
    AdvancementHolder getRootAdvancement();
}
