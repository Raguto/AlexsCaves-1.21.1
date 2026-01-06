package com.github.alexmodguy.alexscaves.server.enchantment;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ACEnchantmentRegistry {
    
    public static final ResourceKey<Enchantment> FIELD_EXTENSION = key("field_extension");
    public static final ResourceKey<Enchantment> CRYSTALLIZATION = key("crystallization");
    public static final ResourceKey<Enchantment> FERROUS_HASTE = key("ferrous_haste");
    public static final ResourceKey<Enchantment> ARROW_INDUCTING = key("arrow_inducting");
    public static final ResourceKey<Enchantment> HEAVY_SLAM = key("heavy_slam");
    public static final ResourceKey<Enchantment> SWIFTWOOD = key("swiftwood");
    public static final ResourceKey<Enchantment> BONKING = key("bonking");
    public static final ResourceKey<Enchantment> DAZING_SWEEP = key("dazing_sweep");
    public static final ResourceKey<Enchantment> PLUMMETING_FLIGHT = key("plummeting_flight");
    public static final ResourceKey<Enchantment> HERD_PHALANX = key("herd_phalanx");
    public static final ResourceKey<Enchantment> CHOMPING_SPIRIT = key("chomping_spirit");
    public static final ResourceKey<Enchantment> ENERGY_EFFICIENCY = key("energy_efficiency");
    public static final ResourceKey<Enchantment> SOLAR = key("solar");
    public static final ResourceKey<Enchantment> X_RAY = key("x_ray");
    public static final ResourceKey<Enchantment> GAMMA_RAY = key("gamma_ray");
    public static final ResourceKey<Enchantment> SECOND_WAVE = key("second_wave");
    public static final ResourceKey<Enchantment> FLINGING = key("flinging");
    public static final ResourceKey<Enchantment> SEA_SWING = key("sea_swing");
    public static final ResourceKey<Enchantment> TSUNAMI = key("tsunami");
    public static final ResourceKey<Enchantment> CHARTING_CALL = key("charting_call");
    public static final ResourceKey<Enchantment> LASTING_MORALE = key("lasting_morale");
    public static final ResourceKey<Enchantment> TAXING_BELLOW = key("taxing_bellow");
    public static final ResourceKey<Enchantment> ENVELOPING_BUBBLE = key("enveloping_bubble");
    public static final ResourceKey<Enchantment> BOUNCING_BOLT = key("bouncing_bolt");
    public static final ResourceKey<Enchantment> SEAPAIRING = key("seapairing");
    public static final ResourceKey<Enchantment> TRIPLE_SPLASH = key("triple_splash");
    public static final ResourceKey<Enchantment> SOAK_SEEKING = key("soak_seeking");
    public static final ResourceKey<Enchantment> DETONATING_DEATH = key("detonating_death");
    public static final ResourceKey<Enchantment> RAPID_POSSESSION = key("rapid_possession");
    public static final ResourceKey<Enchantment> SIGHTLESS = key("sightless");
    public static final ResourceKey<Enchantment> ASTRAL_TRANSFERRING = key("astral_transferring");
    public static final ResourceKey<Enchantment> IMPENDING_STAB = key("impending_stab");
    public static final ResourceKey<Enchantment> SATED_BLADE = key("sated_blade");
    public static final ResourceKey<Enchantment> DOUBLE_STAB = key("double_stab");
    public static final ResourceKey<Enchantment> PRECISE_VOLLEY = key("precise_volley");
    public static final ResourceKey<Enchantment> DARK_NOCK = key("dark_nock");
    public static final ResourceKey<Enchantment> RELENTLESS_DARKNESS = key("relentless_darkness");
    public static final ResourceKey<Enchantment> TWILIGHT_PERFECTION = key("twilight_perfection");
    public static final ResourceKey<Enchantment> SHADED_RESPITE = key("shaded_respite");
    public static final ResourceKey<Enchantment> TARGETED_RICOCHET = key("targeted_ricochet");
    public static final ResourceKey<Enchantment> TRIPLE_SPLIT = key("triple_split");
    public static final ResourceKey<Enchantment> BOUNCY_BALL = key("bouncy_ball");
    public static final ResourceKey<Enchantment> EXPLOSIVE_FLAVOR = key("explosive_flavor");
    public static final ResourceKey<Enchantment> FAR_FLUNG = key("far_flung");
    public static final ResourceKey<Enchantment> SHARP_CANE = key("sharp_cane");
    public static final ResourceKey<Enchantment> STRAIGHT_HOOK = key("straight_hook");
    public static final ResourceKey<Enchantment> SPELL_LASTING = key("spell_lasting");
    public static final ResourceKey<Enchantment> PEPPERMINT_PUNTING = key("peppermint_punting");
    public static final ResourceKey<Enchantment> HUMUNGOUS_HEX = key("humungous_hex");
    public static final ResourceKey<Enchantment> MULTIPLE_MINT = key("multiple_mint");
    public static final ResourceKey<Enchantment> SEEKCANDY = key("seekcandy");

    public static final ResourceKey<Enchantment> GALENA_GAUNTLET = FIELD_EXTENSION;
    public static final ResourceKey<Enchantment> RESISTOR_SHIELD = CRYSTALLIZATION;
    public static final ResourceKey<Enchantment> PRIMITIVE_CLUB = BONKING;
    public static final ResourceKey<Enchantment> EXTINCTION_SPEAR = PLUMMETING_FLIGHT;
    public static final ResourceKey<Enchantment> RAYGUN = ENERGY_EFFICIENCY;
    public static final ResourceKey<Enchantment> ORTHOLANCE = FLINGING;
    public static final ResourceKey<Enchantment> MAGIC_CONCH = CHARTING_CALL;
    public static final ResourceKey<Enchantment> SEA_STAFF = SEA_SWING;
    public static final ResourceKey<Enchantment> TOTEM_OF_POSSESSION = RAPID_POSSESSION;
    public static final ResourceKey<Enchantment> DESOLATE_DAGGER = IMPENDING_STAB;
    public static final ResourceKey<Enchantment> DREADBOW = PRECISE_VOLLEY;
    public static final ResourceKey<Enchantment> SHOT_GUM = TARGETED_RICOCHET;
    public static final ResourceKey<Enchantment> CANDY_CANE_HOOK = SHARP_CANE;
    public static final ResourceKey<Enchantment> SUGAR_STAFF = SPELL_LASTING;

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name));
    }
    
    public static void addAllEnchantsToCreativeTab(CreativeModeTab.Output output, ResourceKey<Enchantment> itemEnchantKey) {
    }
    
    public static List<ResourceKey<Enchantment>> getEnchantsForItem(ResourceKey<Enchantment> itemKey) {
        List<ResourceKey<Enchantment>> enchants = new ArrayList<>();
        
        if (itemKey == GALENA_GAUNTLET) {
            enchants.add(FIELD_EXTENSION);
            enchants.add(CRYSTALLIZATION);
            enchants.add(FERROUS_HASTE);
            enchants.add(ARROW_INDUCTING);
            enchants.add(HEAVY_SLAM);
        } else if (itemKey == RESISTOR_SHIELD) {
            enchants.add(CRYSTALLIZATION);
            enchants.add(BOUNCING_BOLT);
        } else if (itemKey == PRIMITIVE_CLUB) {
            enchants.add(SWIFTWOOD);
            enchants.add(BONKING);
            enchants.add(DAZING_SWEEP);
        } else if (itemKey == EXTINCTION_SPEAR) {
            enchants.add(PLUMMETING_FLIGHT);
            enchants.add(HERD_PHALANX);
            enchants.add(CHOMPING_SPIRIT);
        } else if (itemKey == RAYGUN) {
            enchants.add(ENERGY_EFFICIENCY);
            enchants.add(SOLAR);
            enchants.add(X_RAY);
            enchants.add(GAMMA_RAY);
            enchants.add(SECOND_WAVE);
        } else if (itemKey == ORTHOLANCE) {
            enchants.add(FLINGING);
            enchants.add(SEA_SWING);
            enchants.add(TSUNAMI);
        } else if (itemKey == MAGIC_CONCH) {
            enchants.add(CHARTING_CALL);
            enchants.add(LASTING_MORALE);
            enchants.add(TAXING_BELLOW);
        } else if (itemKey == SEA_STAFF) {
            enchants.add(SEA_SWING);
            enchants.add(ENVELOPING_BUBBLE);
            enchants.add(SEAPAIRING);
            enchants.add(TRIPLE_SPLASH);
            enchants.add(SOAK_SEEKING);
        } else if (itemKey == TOTEM_OF_POSSESSION) {
            enchants.add(DETONATING_DEATH);
            enchants.add(RAPID_POSSESSION);
            enchants.add(SIGHTLESS);
            enchants.add(ASTRAL_TRANSFERRING);
        } else if (itemKey == DESOLATE_DAGGER) {
            enchants.add(IMPENDING_STAB);
            enchants.add(SATED_BLADE);
            enchants.add(DOUBLE_STAB);
        } else if (itemKey == DREADBOW) {
            enchants.add(PRECISE_VOLLEY);
            enchants.add(DARK_NOCK);
            enchants.add(RELENTLESS_DARKNESS);
            enchants.add(TWILIGHT_PERFECTION);
            enchants.add(SHADED_RESPITE);
        } else if (itemKey == SHOT_GUM) {
            enchants.add(TARGETED_RICOCHET);
            enchants.add(TRIPLE_SPLIT);
            enchants.add(BOUNCY_BALL);
            enchants.add(EXPLOSIVE_FLAVOR);
        } else if (itemKey == CANDY_CANE_HOOK) {
            enchants.add(FAR_FLUNG);
            enchants.add(SHARP_CANE);
            enchants.add(STRAIGHT_HOOK);
        } else if (itemKey == SUGAR_STAFF) {
            enchants.add(SPELL_LASTING);
            enchants.add(PEPPERMINT_PUNTING);
            enchants.add(HUMUNGOUS_HEX);
            enchants.add(MULTIPLE_MINT);
            enchants.add(SEEKCANDY);
        }
        
        return enchants;
    }
    
    public static ItemStack createEnchantedBook(HolderLookup.Provider registries, ResourceKey<Enchantment> enchantKey) {
        Optional<HolderLookup.RegistryLookup<Enchantment>> lookupOpt = registries.lookup(Registries.ENCHANTMENT);
        if (lookupOpt.isPresent()) {
            Optional<Holder.Reference<Enchantment>> holderOpt = lookupOpt.get().get(enchantKey);
            if (holderOpt.isPresent()) {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                book.enchant(holderOpt.get(), holderOpt.get().value().getMaxLevel());
                return book;
            }
        }
        return ItemStack.EMPTY;
    }
    
    public static List<ResourceKey<Enchantment>> getAllEnchantmentKeys() {
        List<ResourceKey<Enchantment>> keys = new ArrayList<>();
        keys.add(FIELD_EXTENSION);
        keys.add(CRYSTALLIZATION);
        keys.add(FERROUS_HASTE);
        keys.add(ARROW_INDUCTING);
        keys.add(HEAVY_SLAM);
        keys.add(SWIFTWOOD);
        keys.add(BONKING);
        keys.add(DAZING_SWEEP);
        keys.add(PLUMMETING_FLIGHT);
        keys.add(HERD_PHALANX);
        keys.add(CHOMPING_SPIRIT);
        keys.add(ENERGY_EFFICIENCY);
        keys.add(SOLAR);
        keys.add(X_RAY);
        keys.add(GAMMA_RAY);
        keys.add(SECOND_WAVE);
        keys.add(FLINGING);
        keys.add(SEA_SWING);
        keys.add(TSUNAMI);
        keys.add(CHARTING_CALL);
        keys.add(LASTING_MORALE);
        keys.add(TAXING_BELLOW);
        keys.add(ENVELOPING_BUBBLE);
        keys.add(BOUNCING_BOLT);
        keys.add(SEAPAIRING);
        keys.add(TRIPLE_SPLASH);
        keys.add(SOAK_SEEKING);
        keys.add(DETONATING_DEATH);
        keys.add(RAPID_POSSESSION);
        keys.add(SIGHTLESS);
        keys.add(ASTRAL_TRANSFERRING);
        keys.add(IMPENDING_STAB);
        keys.add(SATED_BLADE);
        keys.add(DOUBLE_STAB);
        keys.add(PRECISE_VOLLEY);
        keys.add(DARK_NOCK);
        keys.add(RELENTLESS_DARKNESS);
        keys.add(TWILIGHT_PERFECTION);
        keys.add(SHADED_RESPITE);
        keys.add(TARGETED_RICOCHET);
        keys.add(TRIPLE_SPLIT);
        keys.add(BOUNCY_BALL);
        keys.add(EXPLOSIVE_FLAVOR);
        keys.add(FAR_FLUNG);
        keys.add(SHARP_CANE);
        keys.add(STRAIGHT_HOOK);
        keys.add(SPELL_LASTING);
        keys.add(PEPPERMINT_PUNTING);
        keys.add(HUMUNGOUS_HEX);
        keys.add(MULTIPLE_MINT);
        keys.add(SEEKCANDY);
        return keys;
    }
}
