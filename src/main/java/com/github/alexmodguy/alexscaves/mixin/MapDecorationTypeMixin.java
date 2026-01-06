package com.github.alexmodguy.alexscaves.mixin;

import org.spongepowered.asm.mixin.Mixin;

// TODO: MapDecoration.Type was changed from enum to registry-based system in 1.21
// Custom map decoration types should now be registered via MapDecorationType registry
// See ACVanillaMapUtil for the new registration approach
@Mixin(targets = "net.minecraft.world.level.saveddata.maps.MapDecorationType")
public class MapDecorationTypeMixin {
    // Enum extension no longer needed - use registry instead
}
