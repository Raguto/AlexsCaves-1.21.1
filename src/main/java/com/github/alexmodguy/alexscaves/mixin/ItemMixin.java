package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void injectRarityColor(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        Component base = cir.getReturnValue();
        Rarity rarity = stack.getRarity();

        if (rarity == ACItemRegistry.RARITY_DEMONIC) {
            cir.setReturnValue(base.copy().withStyle(ChatFormatting.DARK_RED));
            return;
        }

        if (rarity == ACItemRegistry.RARITY_NUCLEAR) {
            cir.setReturnValue(base.copy().withStyle(ChatFormatting.GREEN));
            return;
        }

        if (rarity == ACItemRegistry.RARITY_SWEET) {
            cir.setReturnValue(
                    base.copy().withStyle(Style.EMPTY.withColor(0xFF8ACD))
            );
            return;
        }

        if (rarity == ACItemRegistry.RARITY_RAINBOW) {
            float hue = (System.currentTimeMillis() % 5000) / 5000F;
            int rgb = Color.HSBtoRGB(hue, 1F, 1F);
            cir.setReturnValue(base.copy().withStyle(s -> s.withColor(rgb)));
        }
    }
}
