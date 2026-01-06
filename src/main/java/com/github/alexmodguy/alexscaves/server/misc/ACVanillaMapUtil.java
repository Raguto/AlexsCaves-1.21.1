package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class ACVanillaMapUtil {
    // In 1.21, MapDecoration.Type is replaced with MapDecorationType which is registry-based
    public static final ResourceKey<MapDecorationType> UNDERGROUND_CABIN_MAP_DECORATION_KEY = 
        ResourceKey.create(Registries.MAP_DECORATION_TYPE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "underground_cabin"));

    // This will need to be registered via datapack or registry in 1.21
    // For now, return a placeholder value
    public static byte getMapIconRenderOrdinal(Holder<MapDecorationType> type) {
        if (type != null && type.is(UNDERGROUND_CABIN_MAP_DECORATION_KEY)) {
            return 0;
        }
        return -1;
    }
}
