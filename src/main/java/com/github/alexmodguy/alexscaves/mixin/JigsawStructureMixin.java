package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {

    @Shadow @Final private Optional<ResourceLocation> startJigsawName;
    
    @Shadow @Final private Holder<net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool> startPool;

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        
        String poolName = startPool.unwrapKey().map(key -> key.location().toString()).orElse("");
        String jigsawName = this.startJigsawName.map(ResourceLocation::toString).orElse("");
        
        boolean isTrialChamber = poolName.contains("trial_chambers") || jigsawName.contains("trial_chambers");
        
        boolean isAncientCity = jigsawName.equals("minecraft:city_anchor") || poolName.contains("ancient_city");
        
        if (isTrialChamber || isAncientCity) {
            if (ACBiomeRarity.getACBiomeForPosition(context.seed(), x, z) != null) {
                cir.setReturnValue(Optional.empty());
                return;
            }
        }

        if (isAncientCity) {
            for (Holder<Biome> holder : ACMath.getBiomesWithinAtY(context.biomeSource(), x, context.chunkGenerator().getSeaLevel() - 80, z, 50, context.randomState().sampler())) {
                if (holder.is(ACTagRegistry.HAS_NO_ANCIENT_CITIES_IN)) {
                    cir.setReturnValue(Optional.empty());
                    return;
                }
            }
        }

        if (isTrialChamber) {
            for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(x, context.chunkGenerator().getSeaLevel() - 40, z, 50, context.randomState().sampler())) {
                if (holder.is(ACTagRegistry.HAS_NO_VANILLA_STRUCTURES_IN)) {
                    cir.setReturnValue(Optional.empty());
                    return;
                }
            }
        }
    }
}
