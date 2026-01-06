package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACPotPatternRegistry {

    public static final DeferredRegister<DecoratedPotPattern> DEF_REG = DeferredRegister.create(Registries.DECORATED_POT_PATTERN, AlexsCaves.MODID);

    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> DINOSAUR = DEF_REG.register("dinosaur_pottery_pattern", 
        () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "dinosaur_pottery_pattern")));
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> FOOTPRINT = DEF_REG.register("footprint_pottery_pattern", 
        () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "footprint_pottery_pattern")));
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> GUARDIAN = DEF_REG.register("guardian_pottery_pattern", 
        () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "guardian_pottery_pattern")));
    public static final DeferredHolder<DecoratedPotPattern, DecoratedPotPattern> HERO = DEF_REG.register("hero_pottery_pattern", 
        () -> new DecoratedPotPattern(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "hero_pottery_pattern")));

    public static void expandVanillaDefinitions() {
        // In 1.21, decorated pot patterns are fully data-driven via the registry
        // The ITEM_TO_POT_TEXTURE map is no longer directly modifiable
        // Sherds are linked to patterns via the item's registry name matching the pattern
        // This method is now a no-op as the registration is handled by the DeferredRegister
    }
}
