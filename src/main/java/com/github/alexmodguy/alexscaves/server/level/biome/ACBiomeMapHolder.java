package com.github.alexmodguy.alexscaves.server.level.biome;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
    
    // Store the registry access for lazy initialization
    private static volatile RegistryAccess storedRegistryAccess = null;

    public static Holder<Biome> getBiomeHolder(ResourceKey<Biome> key) {
        ensureInitialized();
        return biomeMap.get(key);
    }

    public static Map<ResourceKey<Biome>, Holder<Biome>> getBiomeMap() {
        ensureInitialized();
        return biomeMap;
    }

    public static boolean isInitialized() {
        // Try to initialize if not already done
        if (!initialized || biomeMap.isEmpty()) {
            ensureInitialized();
        }
        return initialized && !biomeMap.isEmpty();
    }

    private static void ensureInitialized() {
        if (!initialized || biomeMap.isEmpty()) {
            synchronized (lock) {
                if (!initialized || biomeMap.isEmpty()) {
                    tryInitializeFromServer();
                }
            }
        }
    }

    private static void tryInitializeFromServer() {
        // First try stored registry access
        if (storedRegistryAccess != null) {
            try {
                Registry<Biome> biomeRegistry = storedRegistryAccess.registryOrThrow(Registries.BIOME);
                initializeFromRegistryInternal(biomeRegistry);
                return;
            } catch (Exception e) {
                // Silently continue to fallback
            }
        }
        
        // Fall back to server lifecycle hooks
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            try {
                Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
                initializeFromRegistryInternal(biomeRegistry);
            } catch (Exception e) {
                // Silently fail
            }
        }
    }
    
    private static void initializeFromRegistryInternal(Registry<Biome> biomeRegistry) {
        biomeMap.clear();
        for (ResourceKey<Biome> biomeResourceKey : biomeRegistry.registryKeySet()) {
            Optional<Holder.Reference<Biome>> holderOptional = biomeRegistry.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        initialized = true;
    }

    public static void initializeFromRegistry(Registry<Biome> biomeRegistry) {
        synchronized (lock) {
            initializeFromRegistryInternal(biomeRegistry);
        }
    }
    
    /**
     * Store the registry access for later lazy initialization.
     * This should be called as early as possible during world creation.
     */
    public static void setRegistryAccess(RegistryAccess registryAccess) {
        storedRegistryAccess = registryAccess;
    }

    public static void reset() {
        synchronized (lock) {
            biomeMap.clear();
            initialized = false;
            storedRegistryAccess = null;
        }
    }
}
