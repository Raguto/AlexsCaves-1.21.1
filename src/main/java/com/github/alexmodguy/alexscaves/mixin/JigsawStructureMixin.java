package com.github.alexmodguy.alexscaves.mixin;

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

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (this.startJigsawName.isPresent()) {
            String jigsawName = this.startJigsawName.get().toString();
            int i = context.chunkPos().getBlockX(9);
            int j = context.chunkPos().getBlockZ(9);

            if (jigsawName.equals("minecraft:city_anchor")) {
                for (Holder<Biome> holder : ACMath.getBiomesWithinAtY(context.biomeSource(), i, context.chunkGenerator().getSeaLevel() - 80, j, 80, context.randomState().sampler())) {
                    if (holder.is(ACTagRegistry.HAS_NO_ANCIENT_CITIES_IN)) {
                        cir.setReturnValue(Optional.empty());
                        return;
                    }
                }
            }

            if (jigsawName.equals("minecraft:trial_chambers/spawner/contents/all") || jigsawName.contains("trial_chambers")) {
                // Extended radius (80 blocks) to prevent trial chambers from starting near AC biomes
                for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(i, context.chunkGenerator().getSeaLevel() - 40, j, 80, context.randomState().sampler())) {
                    if (holder.is(ACTagRegistry.HAS_NO_VANILLA_STRUCTURES_IN)) {
                        cir.setReturnValue(Optional.empty());
                        return;
                    }
                }
            }
        }
    }
}
