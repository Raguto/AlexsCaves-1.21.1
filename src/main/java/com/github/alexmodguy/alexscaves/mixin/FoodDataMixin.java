package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.PrimordialArmorItem;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    public abstract void eat(int nutrition, float saturation);

    // TODO: FoodData.eat method signature changed in 1.21
    // The method now takes different parameters - needs review
    // @Inject(
    //         method = {"eat"},
    //         cancellable = true,
    //         at = @At(value = "HEAD")
    // )
    // public void ac_eat(ItemStack stack, FoodProperties foodProperties, CallbackInfo ci) {
    //     // Implementation needs update for 1.21 food system
    // }
}
