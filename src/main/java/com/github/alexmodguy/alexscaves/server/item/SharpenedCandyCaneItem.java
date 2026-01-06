package com.github.alexmodguy.alexscaves.server.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SharpenedCandyCaneItem extends Item {

    private static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_damage");
    private static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_speed");

    public SharpenedCandyCaneItem(Properties properties) {
        super(properties);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtEntity, LivingEntity player) {
        if (player instanceof Player player1 && !player1.isCreative()) {
            stack.shrink(1);
            player1.playSound(SoundEvents.ITEM_BREAK);
        }
        hurtEntity.knockback(0.15F, hurtEntity.getX() - player.getX(), hurtEntity.getZ() - player.getZ());
        if(!hurtEntity.level().isClientSide && hurtEntity.level() instanceof ServerLevel serverLevel){
            ItemParticleOption itemParticleOption = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ACItemRegistry.SHARPENED_CANDY_CANE.get()));
            Vec3 hurtCenter = hurtEntity.position().add(0, hurtEntity.getBbHeight() * 0.5F, 0);
            Vec3 playerCenter = player.position().add(0, player.getBbHeight() * 0.5F, 0);
            Vec3 particlePos = playerCenter.subtract(hurtCenter).normalize().scale(hurtEntity.getBbWidth() * 0.6F).add(hurtCenter);
            serverLevel.sendParticles(itemParticleOption, particlePos.x, particlePos.y, particlePos.z, 15, 0.3D, hurtEntity.getRandom().nextFloat() * 0.1F - 0.05F, 0.2F, hurtEntity.getRandom().nextFloat() * 0.1F - 0.05F);
        }
        return true;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.0, AttributeModifier.Operation.ADD_VALUE));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, 4.0, AttributeModifier.Operation.ADD_VALUE));
            return builder.build();
        }
        return ImmutableMultimap.of();
    }

}
