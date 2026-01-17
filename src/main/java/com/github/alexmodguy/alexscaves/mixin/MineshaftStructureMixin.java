package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MineshaftStructure.class)
public class MineshaftStructureMixin {

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/MineshaftStructure;findGenerationPoint(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Ljava/util/Optional;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_findGenerationPoint(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        
        if (ACBiomeRarity.getACBiomeForPosition(context.seed(), x, z) != null) {
            cir.setReturnValue(Optional.empty());
            return;
        }

        for (Holder<Biome> holder : context.biomeSource().getBiomesWithin(x, context.chunkGenerator().getSeaLevel() - 40, z, 50, context.randomState().sampler())) {
            if (holder.is(ACTagRegistry.HAS_NO_VANILLA_STRUCTURES_IN)) {
                cir.setReturnValue(Optional.empty());
                return;
            }
        }
    }
}
