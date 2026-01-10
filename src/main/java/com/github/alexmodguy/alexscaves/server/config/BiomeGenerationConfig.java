package com.github.alexmodguy.alexscaves.server.config;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.github.alexthe666.citadel.Citadel;
import com.google.common.reflect.TypeToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BiomeGenerationConfig {
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    
    private static final int CONFIG_VERSION = 3;

    private static final String OVERWORLD = "minecraft:overworld";

    private static final BiomeGenerationNoiseCondition MAGNETIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(400).alexscavesRarityOffset(0).continentalness(-1F, 1F).depth(0.1F, 2F).build();
    private static final BiomeGenerationNoiseCondition PRIMORDIAL_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(450).alexscavesRarityOffset(1).continentalness(0.4F, 1F).depth(0.15F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition TOXIC_CAVES_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(650).alexscavesRarityOffset(2).continentalness(0.5F, 1F).depth(0.3F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition ABYSSAL_CHASM_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(400).alexscavesRarityOffset(3).continentalness(-0.95F, -0.65F).temperature(-1.0F, 0.5F).depth(0.2F, 1.5F).build();
    private static final BiomeGenerationNoiseCondition FORLORN_HOLLOWS_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(650).alexscavesRarityOffset(4).continentalness(-1F, 1F).depth(0.1F, 2F).build();
    private static final BiomeGenerationNoiseCondition CANDY_CAVITY_CONDITION = new BiomeGenerationNoiseCondition.Builder()
            .dimensions(OVERWORLD).distanceFromSpawn(500).alexscavesRarityOffset(5).continentalness(0.5F, 1F).depth(0.15F, 1.5F).build();
    public static final LinkedHashMap<ResourceKey<Biome>, BiomeGenerationNoiseCondition> BIOMES = new LinkedHashMap<>();

    public static void reloadConfig() {
        // Check version and delete old configs if needed BEFORE loading
        checkAndUpdateConfigVersion();
        
        BIOMES.put(ACBiomeRegistry.MAGNETIC_CAVES, getConfigData("magnetic_caves", MAGNETIC_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.PRIMORDIAL_CAVES, getConfigData("primordial_caves", PRIMORDIAL_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.TOXIC_CAVES, getConfigData("toxic_caves", TOXIC_CAVES_CONDITION));
        BIOMES.put(ACBiomeRegistry.ABYSSAL_CHASM, getConfigData("abyssal_chasm", ABYSSAL_CHASM_CONDITION));
        BIOMES.put(ACBiomeRegistry.FORLORN_HOLLOWS, getConfigData("forlorn_hollows", FORLORN_HOLLOWS_CONDITION));
        BIOMES.put(ACBiomeRegistry.CANDY_CAVITY, getConfigData("candy_cavity", CANDY_CAVITY_CONDITION));
    }

    @Nullable
    @Deprecated(forRemoval = true, since="1.21")
    public static ResourceKey<Biome> getBiomeForEvent(Object event) {
        return null;
    }

    public static int getBiomeCount() {
        return BIOMES.size();
    }

    public static boolean isBiomeDisabledCompletely(ResourceKey<Biome> biome){
        BiomeGenerationNoiseCondition noiseCondition = BIOMES.get(biome);
        return noiseCondition != null && noiseCondition.isDisabledCompletely();
    }

    private static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type, Predicate<T> isInvalid) {
        File configFile = new File(configDir, configName + ".json");
        if (!configFile.exists()) {
            try {
                FileUtils.write(configFile, GSON.toJson(defaults));
            } catch (IOException e) {
                Citadel.LOGGER.error("Biome Generation Config: Could not write " + configFile, e);
            }
        }
        try {
            T found = GSON.fromJson(FileUtils.readFileToString(configFile), type);
            if (isInvalid.test(found)) {
                Citadel.LOGGER.warn("Old Biome Generation Config format found for " + configName + ", replacing with new one.");
                try {
                    FileUtils.write(configFile, GSON.toJson(defaults));
                } catch (IOException e) {
                    Citadel.LOGGER.error("Biome Generation Config: Could not write " + configFile, e);
                }
            } else {
                return found;
            }
        } catch (Exception e) {
            Citadel.LOGGER.error("Biome Generation Config: Could not load " + configFile, e);
        }

        return defaults;
    }

    private static File getConfigDirectory() {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "alexscaves_biome_generation");
        return jsonPath.toFile();
    }
    
    /**
     * Checks if config version has changed and deletes old configs if needed.
     * This ensures users get fresh configs when defaults change significantly.
     */
    private static void checkAndUpdateConfigVersion() {
        File configDir = getConfigDirectory();
        File versionFile = new File(configDir, ".version");
        
        int existingVersion = 0;
        if (versionFile.exists()) {
            try {
                String content = FileUtils.readFileToString(versionFile, "UTF-8").trim();
                existingVersion = Integer.parseInt(content);
            } catch (Exception e) {
                Citadel.LOGGER.warn("Could not read config version file, will regenerate configs");
            }
        }
        
        if (existingVersion < CONFIG_VERSION) {
            Citadel.LOGGER.info("Alex's Caves config version changed ({} -> {}), regenerating biome configs...", 
                existingVersion, CONFIG_VERSION);
            
            // Delete all existing biome config files
            if (configDir.exists()) {
                File[] configFiles = configDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (configFiles != null) {
                    for (File file : configFiles) {
                        if (file.delete()) {
                            Citadel.LOGGER.info("Deleted old config: {}", file.getName());
                        }
                    }
                }
            } else {
                configDir.mkdirs();
            }
            
            // Write new version file
            try {
                FileUtils.write(versionFile, String.valueOf(CONFIG_VERSION), "UTF-8");
            } catch (IOException e) {
                Citadel.LOGGER.error("Could not write config version file", e);
            }
        }
    }

    private static BiomeGenerationNoiseCondition getConfigData(String fileName, BiomeGenerationNoiseCondition defaultConfigData) {
        BiomeGenerationNoiseCondition configData = getOrCreateConfigFile(getConfigDirectory(), fileName, defaultConfigData, new TypeToken<BiomeGenerationNoiseCondition>() {
        }.getType(), BiomeGenerationNoiseCondition::isInvalid);
        return configData;
    }
}
