package com.github.alexmodguy.alexscaves.server.command;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = AlexsCaves.MODID)
public class ACCommands {

    private static final Map<String, ResourceKey<Biome>> BIOME_NAMES = new HashMap<>();
    
    static {
        BIOME_NAMES.put("primordial_caves", ACBiomeRegistry.PRIMORDIAL_CAVES);
        BIOME_NAMES.put("magnetic_caves", ACBiomeRegistry.MAGNETIC_CAVES);
        BIOME_NAMES.put("toxic_caves", ACBiomeRegistry.TOXIC_CAVES);
        BIOME_NAMES.put("abyssal_chasm", ACBiomeRegistry.ABYSSAL_CHASM);
        BIOME_NAMES.put("forlorn_hollows", ACBiomeRegistry.FORLORN_HOLLOWS);
        BIOME_NAMES.put("candy_cavity", ACBiomeRegistry.CANDY_CAVITY);
    }

    private static final SuggestionProvider<CommandSourceStack> BIOME_SUGGESTIONS = (context, builder) -> 
        SharedSuggestionProvider.suggest(BIOME_NAMES.keySet(), builder);

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(Commands.literal("locateacbiome")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("biome", StringArgumentType.word())
                .suggests(BIOME_SUGGESTIONS)
                .executes(context -> {
                    String biomeName = StringArgumentType.getString(context, "biome");
                    return locateACBiome(context.getSource(), biomeName);
                })
            )
        );
    }

    private static int locateACBiome(CommandSourceStack source, String biomeName) {
        ResourceKey<Biome> biomeKey = BIOME_NAMES.get(biomeName.toLowerCase());
        
        if (biomeKey == null) {
            source.sendFailure(Component.literal("Unknown Alex's Caves biome: " + biomeName));
            source.sendFailure(Component.literal("Available biomes: " + String.join(", ", BIOME_NAMES.keySet())));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos playerPos = BlockPos.containing(source.getPosition());
        
        source.sendSuccess(() -> Component.literal("Searching for " + biomeName + "..."), false);
        
        // Use ACBiomeRarity to find the biome location
        BlockPos biomePos = findACBiome(level, playerPos, biomeKey);
        
        if (biomePos != null) {
            int distance = Mth.floor(Math.sqrt(playerPos.distSqr(biomePos)));
            Component coordsText = Component.literal("[" + biomePos.getX() + ", " + biomePos.getZ() + "]")
                .setStyle(Style.EMPTY
                    .withColor(0x55FF55)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, 
                        "/tp @s " + biomePos.getX() + " ~ " + biomePos.getZ()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        Component.literal("Click to teleport"))));
            
            source.sendSuccess(() -> Component.literal("Found " + biomeName + " at ")
                .append(coordsText)
                .append(Component.literal(" (" + distance + " blocks away)")), false);
            return 1;
        } else {
            source.sendFailure(Component.literal("Could not find " + biomeName + " within search range"));
            return 0;
        }
    }

    private static BlockPos findACBiome(ServerLevel level, BlockPos playerPos, ResourceKey<Biome> biomeKey) {
        long seed = level.getSeed();
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;
        
        // Search in expanding squares - check every 4 chunks for speed (AC biomes are large)
        int searchRadius = 500; // Search up to 500 chunks (8000 blocks)
        int step = 4; // Check every 4 chunks
        
        for (int radius = 0; radius < searchRadius; radius += step) {
            for (int dx = -radius; dx <= radius; dx += step) {
                for (int dz = -radius; dz <= radius; dz += step) {
                    // Only check the perimeter of each square
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    
                    int chunkX = playerChunkX + dx;
                    int chunkZ = playerChunkZ + dz;
                    int blockX = chunkX * 16 + 8;
                    int blockZ = chunkZ * 16 + 8;
                    
                    // Check if this location would have the AC biome
                    ResourceKey<Biome> biomeAtPos = ACBiomeRarity.getACBiomeForPosition(seed, blockX, blockZ);
                    if (biomeAtPos != null && biomeAtPos.equals(biomeKey)) {
                        return new BlockPos(blockX, 0, blockZ);
                    }
                }
            }
        }
        
        return null;
    }
}
