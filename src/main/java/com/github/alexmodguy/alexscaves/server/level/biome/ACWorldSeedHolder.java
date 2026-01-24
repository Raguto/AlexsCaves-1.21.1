package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Thread-safe holder for world seed and dimension.
 * Uses volatile to ensure visibility across threads during world generation.
 */
public class ACWorldSeedHolder {
    private static volatile long worldSeed = 0;
    private static volatile ResourceKey<Level> currentDimension = Level.OVERWORLD;
    private static final ThreadLocal<ResourceKey<Level>> threadDimension = new ThreadLocal<>();
    private static volatile boolean initialized = false;

    public static synchronized void setSeed(long seed) {
        worldSeed = seed;
        initialized = true;
    }

    public static long getSeed() {
        return worldSeed;
    }

    public static synchronized void setDimension(ResourceKey<Level> dimension) {
        currentDimension = dimension;
        threadDimension.set(dimension);
    }

    public static ResourceKey<Level> getDimension() {
        ResourceKey<Level> threadValue = threadDimension.get();
        return threadValue != null ? threadValue : currentDimension;
    }

    public static boolean isInitialized() {
        return initialized && worldSeed != 0;
    }

    public static synchronized void reset() {
        worldSeed = 0;
        currentDimension = Level.OVERWORLD;
        threadDimension.remove();
        initialized = false;
    }
}
