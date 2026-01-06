package com.github.alexmodguy.alexscaves.server.inventory;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACMenuRegistry {

    public static final DeferredRegister<MenuType<?>> DEF_REG = DeferredRegister.create(BuiltInRegistries.MENU, AlexsCaves.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SpelunkeryTableMenu>> SPELUNKERY_TABLE_MENU = DEF_REG.register("spelunkery_table_menu", () -> new MenuType<SpelunkeryTableMenu>(SpelunkeryTableMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<NuclearFurnaceMenu>> NUCLEAR_FURNACE_MENU = DEF_REG.register("nuclear_furnace_menu", () -> new MenuType<NuclearFurnaceMenu>(NuclearFurnaceMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
