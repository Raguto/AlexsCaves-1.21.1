package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    
    @Accessor("progress")
    AdvancementProgress getProgress();
    
    @Accessor("parent")
    AdvancementWidget getParent();
    
    @Accessor("advancementHolder")
    AdvancementHolder getAdvancement();
}
