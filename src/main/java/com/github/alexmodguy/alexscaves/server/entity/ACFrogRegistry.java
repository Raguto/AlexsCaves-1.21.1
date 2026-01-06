package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.FrogVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACFrogRegistry {

    public static final DeferredRegister<FrogVariant> DEF_REG = DeferredRegister.create(Registries.FROG_VARIANT, AlexsCaves.MODID);

    public static final DeferredHolder<FrogVariant, FrogVariant> PRIMORDIAL = DEF_REG.register("primordial", () -> new FrogVariant(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/primordial_frog.png")));

}
