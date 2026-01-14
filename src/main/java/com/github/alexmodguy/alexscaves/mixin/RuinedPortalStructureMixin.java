package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RuinedPortalStructure.class)
public class RuinedPortalStructureMixin {

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/RuinedPortalStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int i = context.chunkPos().getBlockX(9);
        int j = context.chunkPos().getBlockZ(9);

        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(i, context.chunkGenerator().getSeaLevel() - 40, j, 80, context.randomState().sampler())) {
            if (holder.is(ACTagRegistry.HAS_NO_VANILLA_STRUCTURES_IN)) {
                cir.setReturnValue(Optional.empty());
                return;
            }
        }
    }
}
