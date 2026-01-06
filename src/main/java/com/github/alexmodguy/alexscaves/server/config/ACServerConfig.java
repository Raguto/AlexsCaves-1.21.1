package com.github.alexmodguy.alexscaves.server.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ACServerConfig {

    public static final ACServerConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        final Pair<ACServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ACServerConfig::new);
        INSTANCE = specPair.getLeft();
        SPEC = specPair.getRight();
    }

    public final ModConfigSpec.DoubleValue caveBiomeMeanWidth;
    public final ModConfigSpec.IntValue caveBiomeMeanSeparation;
    public final ModConfigSpec.DoubleValue caveBiomeWidthRandomness;
    public final ModConfigSpec.DoubleValue caveBiomeSpacingRandomness;
    public final ModConfigSpec.BooleanValue warnGenerationIncompatibility;
    public final ModConfigSpec.DoubleValue caveCreatureSpawnCountModifier;
    public final ModConfigSpec.IntValue pathfindingThreads;
    public final ModConfigSpec.IntValue nucleeperFuseTime;
    public final ModConfigSpec.DoubleValue luxtructosaurusBlockDropChance;
    public final ModConfigSpec.IntValue atlatitanMaxExplosionResistance;
    public final ModConfigSpec.BooleanValue devastatingTremorzillaBeam;
    public final ModConfigSpec.DoubleValue drownedDivingGearSpawnChance;
    public final ModConfigSpec.BooleanValue watcherPossession;
    public final ModConfigSpec.IntValue watcherPossessionCooldown;
    public final ModConfigSpec.BooleanValue walkingOnMagnets;
    public final ModConfigSpec.IntValue amberMonolithMeanTime;
    public final ModConfigSpec.BooleanValue nuclearFurnaceBlastingOnly;
    public final ModConfigSpec.BooleanValue nuclearFurnaceCustomType;
    public final ModConfigSpec.BooleanValue onlyOneResearchNeeded;
    public final ModConfigSpec.IntValue caveMapSearchAttempts;
    public final ModConfigSpec.IntValue caveMapSearchWidth;
    public final ModConfigSpec.IntValue nukeMaxBlockExplosionResistance;
    public final ModConfigSpec.BooleanValue nukesSpawnItemDrops;
    public final ModConfigSpec.DoubleValue nukeExplosionSizeModifier;
    public final ModConfigSpec.BooleanValue totemOfPossessionPlayers;
    public final ModConfigSpec.IntValue darknessCloakChargeTime;
    public final ModConfigSpec.IntValue darknessCloakFlightTime;
    public final ModConfigSpec.BooleanValue sugarRushSlowsTime;
    public final ModConfigSpec.DoubleValue magneticTabletLootChance;
    public final ModConfigSpec.DoubleValue primordialTabletLootChance;
    public final ModConfigSpec.DoubleValue toxicTabletLootChance;
    public final ModConfigSpec.DoubleValue abyssalTabletLootChance;
    public final ModConfigSpec.DoubleValue forlornTabletLootChance;
    public final ModConfigSpec.DoubleValue candyTabletLootChance;
    public final ModConfigSpec.DoubleValue cabinMapLootChance;
    public final ModConfigSpec.BooleanValue cartographersSellCabinMaps;
    public final ModConfigSpec.BooleanValue wanderingTradersSellCabinMaps;
    public final ModConfigSpec.BooleanValue lootChestInWitchHuts;
    public final ModConfigSpec.BooleanValue enchantmentsInLoot;

    public ACServerConfig(final ModConfigSpec.Builder builder) {
        builder.push("generation");
        caveBiomeMeanWidth = builder.comment("Average radius (in blocks) of an Alex's Caves cave biome.").translation("cave_biome_mean_width").defineInRange("cave_biome_mean_width", 300D, 10.0D, Double.MAX_VALUE);
        caveBiomeMeanSeparation = builder.comment("Average separation (in blocks) between each Alex's Caves cave biome.").translation("cave_biome_mean_separation").defineInRange("cave_biome_mean_separation", 900, 50, Integer.MAX_VALUE);
        caveBiomeWidthRandomness = builder.comment("How irregularly shaped Alex's Caves cave biomes can generate. 0 = all biomes nearly circular. 1 = biomes completely squiggly in shape.").translation("cave_biome_width_randomness").defineInRange("cave_biome_width_randomness", 0.15D, 0, 1D);
        caveBiomeSpacingRandomness = builder.comment("Average spacing in between Alex's Caves cave biomes. 0 = all biomes nearly perfectly equidistant. 1 = biomes completely randomly spread out, sometimes next to eachother.").translation("cave_biome_spacing_randomness").defineInRange("cave_biome_spacing_randomness", 0.45D, 0, 1D);
        warnGenerationIncompatibility = builder.comment("Whether to warn users when a server starts if an incompatible generation mod is detected.").translation("warn_generation_incompatibility").define("warn_generation_incompatibility", true);
        builder.pop();
        builder.push("mob-spawning");
        caveCreatureSpawnCountModifier = builder.comment("Cave Creatures (All dinosaurs, raycats, etc) spawn at this frequency. Their cap is calculated by multiplying this number with the default mob cap for surface animals.").translation("cave_creature_spawn_count_modifier").defineInRange("cave_creature_spawn_count_modifier", 1.75D, 0, 10D);
        drownedDivingGearSpawnChance = builder.comment("The percent chance that drowned have to spawn wearing diving gear in the Abyssal Chasm. 0 = no diving gear wearing drowned will spawn").translation("drowned_diving_gear_spawn_chance").defineInRange("drowned_diving_gear_spawn_chance", 0.2D, 0, 1D);
        builder.pop();
        builder.push("mob-behavior");
        pathfindingThreads = builder.comment("How many cpu cores big mobs(tremorzilla, atlatitan, grottoceratops etc) should utilize when pathing. Bigger number = less impact on TPS").translation("pathfinding_threads").defineInRange("pathfinding_threads", 5, 1, 100);
        luxtructosaurusBlockDropChance = builder.comment("Chance that blocks destroyed by luxtructosaurus attacks drop themselves, if mob griefing is enabled.").translation("luxtructosaurus_block_drop_chance").defineInRange("luxtructosaurus_block_drop_chance", 0.75D, 0, 1D);
        atlatitanMaxExplosionResistance = builder.comment("The maximum explosion resistance that a block can have to be destroyed by an atlatitan stomp. Set to zero to disable all atlatitan block breaking.").translation("atlatitan_max_block_explosion_resistance").defineInRange("atlatitan_max_block_explosion_resistance", 10, 0, Integer.MAX_VALUE);
        nucleeperFuseTime = builder.comment("How long (in game ticks) it takes for a nucleeper to explode.").translation("nucleeper_fuse_time").defineInRange("nucleeper_fuse_time", 300, 20, Integer.MAX_VALUE);
        devastatingTremorzillaBeam = builder.comment("True if the Tremorzilla beam breaks even more blocks.").translation("devastating_tremorzilla_beam").define("devastating_tremorzilla_beam", true);
        watcherPossession = builder.comment("Whether the Watcher can take control of the camera.").translation("watcher_possession").define("watcher_possession", true);
        watcherPossessionCooldown = builder.comment("How long (in game ticks) between watcher possession attempts.").translation("watcher_possession_cooldown").defineInRange("watcher_possession_cooldown", 300, 20, 24000);
        builder.pop();
        builder.push("block-behavior");
        walkingOnMagnets = builder.comment("True if players wearing boots can walk on any scarlet neodymium surface.").translation("walking_on_magnets").define("walking_on_magnets", true);
        amberMonolithMeanTime = builder.comment("How long (in game ticks) it usually takes for an amber monolith to spawn an animal.").translation("amber_monolith_mean_time").defineInRange("amber_monolith_mean_time", 32000, 1000, Integer.MAX_VALUE);
        nuclearFurnaceBlastingOnly = builder.comment("True if the Nuclear Furnace only uses 'Blasting' recipes, false to use all smelting recipes.").translation("nuclear_furnace_blasting_only").define("nuclear_furnace_blasting_only", true);
        nuclearFurnaceCustomType = builder.comment("True if the Nuclear Furnace should only use recipes using the `alexscaves:nuclear_furnace` recipe type, false to use regular behavior.").translation("nuclear_furnace_custom_type").define("nuclear_furnace_custom_type", false);
        builder.pop();
        builder.push("item-behavior");
        onlyOneResearchNeeded = builder.comment("True if one Cave Codex is all that is needed to unlock every Cave Compendium entry.").translation("only_one_research_needed").define("only_one_research_needed", false);
        caveMapSearchAttempts = builder.comment("How many attempts to find a biome a cave map engages in when used. Increase this to increase the search radius, or decrease it to make them faster.").translation("cave_map_search_attempts").defineInRange("cave_map_search_attempts", 128000, 64, Integer.MAX_VALUE);
        caveMapSearchWidth = builder.comment("How wide each search attempt scans for a biome. Increasing this generally makes cave biome maps faster - at the cost of losing fidelity(may skip biomes smaller than this in block width).").translation("cave_map_search_width").defineInRange("cave_map_search_width", 64, 4, 256);
        nukeMaxBlockExplosionResistance = builder.comment("The maximum explosion resistance that a block can have to be destroyed by a nuclear explosion. Set to zero to disable all nuclear explosion block breaking.").translation("nuke_max_block_explosion_resistance").defineInRange("nuke_max_block_explosion_resistance", 1000, 0, Integer.MAX_VALUE);
        nukesSpawnItemDrops = builder.comment("Whether some block items are dropped by nuclear explosions. False if all destroyed blocks do not drop items.").translation("nuke_spawn_item_drops").define("nuke_spawn_item_drops", true);
        nukeExplosionSizeModifier = builder.comment("The scale of nuclear bomb destruction. multiply this by 16 to get the radius of a nuclear bomb explosion.").translation("nuclear_explosion_size_modifier").defineInRange("nuclear_explosion_size_modifier", 3.0D, 0.0, Double.MAX_VALUE);
        totemOfPossessionPlayers = builder.comment("Whether the Totem of Possession can be applied to players.").translation("totem_of_possession_works_on_players").define("totem_of_possession_works_on_players", true);
        darknessCloakChargeTime = builder.comment("The amount of time (in ticks) it takes to charge up the Cloak of Darkness ability.").translation("darkness_cloak_charge_time").defineInRange("darkness_cloak_charge_time", 1000, 20, Integer.MAX_VALUE);
        darknessCloakFlightTime = builder.comment("The amount of time (in ticks) that players can fly with the Cloak of Darkness ability.").translation("darkness_cloak_fly_time").defineInRange("darkness_cloak_fly_time", 200, 20, Integer.MAX_VALUE);
        builder.pop();
        builder.push("potion-behavior");
        sugarRushSlowsTime = builder.comment("Whether the Sugar Rush changes the tick rate of the game in the area of affected players.").translation("sugar_rush_slows_time").define("sugar_rush_slows_time", true);
        builder.pop();
        builder.push("vanilla-changes");
        magneticTabletLootChance = builder.comment("percent chance of bastion having a cave tablet for magnetic caves in its loot table:").translation("magnetic_tablet_loot_chance").defineInRange("magnetic_tablet_loot_chance", 0.45D, 0.0, 1.0D);
        primordialTabletLootChance = builder.comment("percent chance of suspicious sand having a cave tablet for primordial caves in its loot table:").translation("primordial_tablet_loot_chance").defineInRange("primordial_tablet_loot_chance", 0.15D, 0.0, 1.0D);
        toxicTabletLootChance = builder.comment("percent chance of jungle temple having a cave tablet for toxic caves in its loot table:").translation("toxic_tablet_loot_chance").defineInRange("toxic_tablet_loot_chance", 0.5D, 0.0, 1.0D);
        abyssalTabletLootChance = builder.comment("percent chance of underwater ruins having a cave tablet for abyssal chasm in its loot table:").translation("abyssal_tablet_loot_chance").defineInRange("abyssal_tablet_loot_chance", 0.4D, 0.0, 1.0D);
        forlornTabletLootChance = builder.comment("percent chance of mansion having a cave tablet for forlorn hollows in its loot table:").translation("forlorn_tablet_loot_chance").defineInRange("forlorn_tablet_loot_chance", 0.75D, 0.0, 1.0D);
        candyTabletLootChance = builder.comment("percent chance of witch hut chest having a cave tablet for candy cavity in its loot table:").translation("candy_cavity_loot_chance").defineInRange("candy_cavity_loot_chance", 0.9D, 0.0, 1.0D);
        cabinMapLootChance = builder.comment("percent chance of abandoned mineshaft chests having a map to a nearby underground mineshaft in their loot table:").translation("cabin_map_loot_chance").defineInRange("cabin_map_loot_chance", 0.15D, 0.0, 1.0D);
        cartographersSellCabinMaps = builder.comment("Whether the Cartographer Villagers can sell maps to Underground Cabins.").translation("cartographers_sell_cabin_maps").define("cartographers_sell_cabin_maps", true);
        wanderingTradersSellCabinMaps = builder.comment("Whether the Wandering Traders can sell maps to Underground Cabins.").translation("wandering_traders_sell_cabin_maps").define("wandering_traders_sell_cabin_maps", true);
        lootChestInWitchHuts = builder.comment("Whether a loot chest is added to vanilla's witch huts. This is included to provide another place to find candy cavity biome cave tablets.").translation("loot_chest_in_witch_huts").define("loot_chest_in_witch_huts", true);
        enchantmentsInLoot = builder.comment("Whether the Enchantments added by AC appear in vanilla loot tables.").translation("enchantments_in_loot").define("enchantments_in_loot", false);
        builder.pop();
    }
}
