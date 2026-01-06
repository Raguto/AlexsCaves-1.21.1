package com.github.alexmodguy.alexscaves.server.level.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ACWorldSeedHolder {
    private static long worldSeed = 0;
    private static ResourceKey<Level> currentDimension = Level.OVERWORLD;
    private static boolean initialized = false;

    public static void setSeed(long seed) {
        worldSeed = seed;
        initialized = true;
    }

    public static long getSeed() {
        return worldSeed;
    }

    public static void setDimension(ResourceKey<Level> dimension) {
        currentDimension = dimension;
    }

    public static ResourceKey<Level> getDimension() {
        return currentDimension;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void reset() {
        worldSeed = 0;
        currentDimension = Level.OVERWORLD;
        initialized = false;
    }
}
