package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexthe666.citadel.item.BlockItemWithSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.List;

public class BlockItemWithSupplierLore extends BlockItemWithSupplier {

    private final DeferredHolder<Block, Block> block;

    public BlockItemWithSupplierLore(DeferredHolder<Block, Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
        this.block = blockSupplier;
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        String blockName = block.getId().getNamespace() + "." + block.getId().getPath();
        tooltip.add(Component.translatable("block." + blockName + ".desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
