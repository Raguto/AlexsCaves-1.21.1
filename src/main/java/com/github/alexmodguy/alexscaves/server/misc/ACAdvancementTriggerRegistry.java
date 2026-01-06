package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACAdvancementTriggerRegistry {

    public static final DeferredRegister<CriterionTrigger<?>> DEF_REG = DeferredRegister.create(Registries.TRIGGER_TYPE, AlexsCaves.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> KILL_MOB_WITH_GALENA_GAUNTLET = DEF_REG.register("kill_mob_with_galena_gauntlet", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> FINISHED_QUARRY = DEF_REG.register("finished_quarry", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DINOSAURS_MINECART = DEF_REG.register("dinosaurs_minecart", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> CAVE_PAINTING = DEF_REG.register("cave_painting", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> MYSTERY_CAVE_PAINTING = DEF_REG.register("mystery_cave_painting", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> SUMMON_LUXTRUCTOSAURUS = DEF_REG.register("summon_luxtructosaurus", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> ATLATITAN_STOMP = DEF_REG.register("atlatitan_stomp", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> ENTER_ACID_WITH_ARMOR = DEF_REG.register("enter_acid_with_armor", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> ACID_CREATE_RUST = DEF_REG.register("acid_create_rust", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> REMOTE_DETONATION = DEF_REG.register("remote_detonation", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> STOP_NUCLEAR_FURNACE_MELTDOWN = DEF_REG.register("stop_nuclear_furnace_meltdown", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> HATCH_TREMORZILLA_EGG = DEF_REG.register("hatch_tremorzilla_egg", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> TREMORZILLA_KILL_BEAM = DEF_REG.register("tremorzilla_kill_beam", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> STALKED_BY_DEEP_ONE = DEF_REG.register("stalked_by_deep_one", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DEEP_ONE_TRADE = DEF_REG.register("deep_one_trade", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DEEP_ONE_NEUTRAL = DEF_REG.register("deep_one_neutral", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DEEP_ONE_HELPFUL = DEF_REG.register("deep_one_helpful", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> UNDERZEALOT_SACRIFICE = DEF_REG.register("underzealot_sacrifice", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> BEHOLDER_FAR_AWAY = DEF_REG.register("beholder_far_away", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> EAT_DARKENED_APPLE = DEF_REG.register("eat_darkened_apple", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> FROSTMINT_EXPLOSION = DEF_REG.register("frostmint_explosion", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> CONVERT_BIOME = DEF_REG.register("convert_biome", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> CONVERT_NETHER_BIOME = DEF_REG.register("convert_nether_biome", ACAdvancementTrigger::new);
    
    // Biome discovery triggers
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_MAGNETIC_CAVES = DEF_REG.register("discover_magnetic_caves", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_PRIMORDIAL_CAVES = DEF_REG.register("discover_primordial_caves", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_TOXIC_CAVES = DEF_REG.register("discover_toxic_caves", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_ABYSSAL_CHASM = DEF_REG.register("discover_abyssal_chasm", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_FORLORN_HOLLOWS = DEF_REG.register("discover_forlorn_hollows", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_CANDY_CAVITY = DEF_REG.register("discover_candy_cavity", ACAdvancementTrigger::new);
    
    // Structure discovery triggers
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_GINGERBREAD_TOWN = DEF_REG.register("discover_gingerbread_town", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_LICOWITCH_TOWER = DEF_REG.register("discover_licowitch_tower", ACAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> DISCOVER_ABYSSAL_RUINS = DEF_REG.register("discover_abyssal_ruins", ACAdvancementTrigger::new);
    
    // Block stepping trigger
    public static final DeferredHolder<CriterionTrigger<?>, ACAdvancementTrigger> WALK_ON_ROCK_CANDY = DEF_REG.register("walk_on_rock_candy", ACAdvancementTrigger::new);
}
