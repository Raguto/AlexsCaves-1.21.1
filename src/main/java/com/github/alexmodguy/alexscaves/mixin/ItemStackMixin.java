package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.rarity.DemonicRarity;
import com.github.alexmodguy.alexscaves.server.item.rarity.NuclearRarity;
import com.github.alexmodguy.alexscaves.server.item.rarity.RainbowRarity;
import com.github.alexmodguy.alexscaves.server.item.rarity.SweetRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void alexscaves$applyCustomRarity(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Item item = stack.getItem();
        Component name = cir.getReturnValue();

        if (item instanceof DemonicRarity)
            cir.setReturnValue(name.copy().withStyle(ChatFormatting.DARK_RED));

        else if (item instanceof NuclearRarity)
            cir.setReturnValue(name.copy().withStyle(ChatFormatting.GREEN));

        else if (item instanceof SweetRarity)
            cir.setReturnValue(name.copy().withStyle(style -> style.withColor(0xFF8ACD)));

        else if (item instanceof RainbowRarity)
            cir.setReturnValue(name.copy().withStyle(style ->
                    style.withColor(Color.HSBtoRGB((System.currentTimeMillis() % 5000) / 5000F, 1f, 1f))
            ));
    }
}
