package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACVanillaMapUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapDecoration.class)
public abstract class MapDecorationMixin {

    @Shadow
    public abstract Holder<MapDecorationType> type();

    // TODO: Map decoration rendering changed in 1.21 - needs NeoForge event or different approach
    // The render method no longer exists on MapDecoration in vanilla
    // Custom map decorations should be registered via MapDecorationType registry
}
