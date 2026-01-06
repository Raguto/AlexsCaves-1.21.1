package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ACBiomeMapHolder {
    private static final Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;
    private static final Object lock = new Object();

    public static Holder<Biome> getBiomeHolder(ResourceKey<Biome> key) {
        ensureInitialized();
        return biomeMap.get(key);
    }

    public static Map<ResourceKey<Biome>, Holder<Biome>> getBiomeMap() {
        ensureInitialized();
        return biomeMap;
    }

    public static boolean isInitialized() {
        return initialized && !biomeMap.isEmpty();
    }

    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    tryInitializeFromServer();
                }
            }
        }
    }

    private static void tryInitializeFromServer() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            initializeFromRegistry(server.registryAccess().registryOrThrow(Registries.BIOME));
        }
    }

    public static void initializeFromRegistry(Registry<Biome> biomeRegistry) {
        synchronized (lock) {
            if (initialized && !biomeMap.isEmpty()) {
                return;
            }
            
            biomeMap.clear();
            for (ResourceKey<Biome> biomeResourceKey : biomeRegistry.registryKeySet()) {
                Optional<Holder.Reference<Biome>> holderOptional = biomeRegistry.getHolder(biomeResourceKey);
                holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
            }
            initialized = true;
        }
    }

    public static void reset() {
        synchronized (lock) {
            biomeMap.clear();
            initialized = false;
        }
    }
}
