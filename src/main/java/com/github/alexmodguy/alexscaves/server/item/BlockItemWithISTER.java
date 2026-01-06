package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.item.BlockItemWithSupplier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockItemWithISTER extends BlockItemWithSupplier {

    public BlockItemWithISTER(DeferredHolder<Block, Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }
}
