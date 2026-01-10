package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;

import javax.annotation.Nullable;
import java.util.UUID;

public class DivingArmorItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B77"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E12"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B43F"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB111")};
    private Multimap<Holder<Attribute>, AttributeModifier> divingArmorAttributes;

    private static final int[] DURABILITY_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final int DURABILITY_MULTIPLIER = 25;

    public DivingArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties().durability(DURABILITY_PER_SLOT[slot.getSlot().getIndex()] * DURABILITY_MULTIPLIER));
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
        builder.put(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE));
        if (slot == Type.LEGGINGS) {
            builder.put(net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "swim_speed"), 0.5D, AttributeModifier.Operation.ADD_VALUE));
        }else if (slot == Type.CHESTPLATE) {
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "armor_toughness"), (double)armorMaterial.value().toughness(), AttributeModifier.Operation.ADD_VALUE));
        }
        float knockbackResistance = armorMaterial.value().knockbackResistance();
        if (knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "knockback_resistance"), (double)knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        divingArmorAttributes = builder.build();
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        return equipmentSlot == this.type.getSlot() ? this.divingArmorAttributes : ImmutableMultimap.of();
    }

    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/diving_suit_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/diving_suit_0.png");
        }
    }
}
