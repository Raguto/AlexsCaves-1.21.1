package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract WorldData getWorldData();

    @Inject(method = "getWorldData", at = @At("RETURN"))
    private void ac_onGetWorldData(CallbackInfoReturnable<WorldData> cir) {
        if (!ACWorldSeedHolder.isInitialized() && cir.getReturnValue() != null) {
            try {
                long seed = cir.getReturnValue().worldGenOptions().seed();
                ACWorldSeedHolder.setSeed(seed);
                
                MinecraftServer server = (MinecraftServer)(Object)this;
                try {
                    Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
                    if (!ACBiomeMapHolder.isInitialized()) {
                        ACBiomeMapHolder.initializeFromRegistry(biomeRegistry);
                    }
                    
                    try {
                        Registry<LevelStem> levelStems = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
                        ImmutableSet.Builder<Holder<Biome>> acBiomesBuilder = ImmutableSet.builder();
                        for (ResourceKey<Biome> biomeKey : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
                            Optional<Holder.Reference<Biome>> holder = biomeRegistry.getHolder(biomeKey);
                            if (holder.isPresent()) {
                                acBiomesBuilder.add(holder.get());
                            }
                        }
                        ImmutableSet<Holder<Biome>> acBiomes = acBiomesBuilder.build();
                        
                        if (!acBiomes.isEmpty()) {
                            for (ResourceKey<LevelStem> levelStemKey : levelStems.registryKeySet()) {
                                Optional<Holder.Reference<LevelStem>> stemHolder = levelStems.getHolder(levelStemKey);
                                if (stemHolder.isPresent()) {
                                    var biomeSource = stemHolder.get().value().generator().getBiomeSource();
                                    if (biomeSource instanceof BiomeSourceAccessor accessor) {
                                        accessor.expandBiomesWith(acBiomes);
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void ac_onStopServer(CallbackInfo ci) {
        ACWorldSeedHolder.reset();
        ACBiomeMapHolder.reset();
    }
}
