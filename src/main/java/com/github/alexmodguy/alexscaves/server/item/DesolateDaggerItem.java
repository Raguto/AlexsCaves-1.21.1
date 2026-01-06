package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DesolateDaggerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class DesolateDaggerItem extends SwordItem {
    public DesolateDaggerItem() {
        super(Tiers.DIAMOND, (new Item.Properties()).rarity(ACItemRegistry.RARITY_DEMONIC).attributes(SwordItem.createAttributes(Tiers.DIAMOND, 0, -2F)));
    }

    public int getMaxDamage(ItemStack stack) {
        return 360;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurt, LivingEntity player) {
        if (super.hurtEnemy(stack, hurt, player)) {
            int delayedLevel = stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.IMPENDING_STAB));
            for(int i = 0; i < 1 + stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.DOUBLE_STAB)); i++){
                DesolateDaggerEntity daggerEntity = ACEntityRegistry.DESOLATE_DAGGER.get().create(player.level());
                daggerEntity.setTargetId(hurt.getId());
                daggerEntity.copyPosition(player);
                daggerEntity.setItemStack(stack);
                daggerEntity.orbitFor = (delayedLevel > 0 ? 40 : 20) + player.getRandom().nextInt(10);
                player.level().addFreshEntity(daggerEntity);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidRepairItem(ItemStack itemStack, ItemStack repairWith) {
        return repairWith.is(ACItemRegistry.PURE_DARKNESS.get());
    }

}
