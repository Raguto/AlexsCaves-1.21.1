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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GingerbreadArmorItem extends ArmorItem {

    private static final double MIN_SPEED_BOOST = 0.1D;
    private static final double MAX_SPEED_BOOST = 1.0D;
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B77"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E12"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B43F"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB111")};
    private Map<Integer, Multimap<Holder<Attribute>, AttributeModifier>> gingerbreadDurabilityDependentAttributes = new HashMap<>();
    private final Multimap<Holder<Attribute>, AttributeModifier> defaultAttributes;

    public GingerbreadArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties());
        ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "movement_speed"), MIN_SPEED_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        defaultAttributes = builder.build();
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getOrCreateDurabilityAttributes(int durabilityIn, int maxDurability) {
        if (gingerbreadDurabilityDependentAttributes.containsKey(durabilityIn)) {
            return gingerbreadDurabilityDependentAttributes.get(durabilityIn);
        } else {
            float scaledDurability = durabilityIn / (float) maxDurability;
            double speed = MIN_SPEED_BOOST + (MAX_SPEED_BOOST - MIN_SPEED_BOOST) * scaledDurability;
            ResourceLocation armorId = ResourceLocation.fromNamespaceAndPath("alexscaves", "armor." + type.getSlot().getName());
            ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ARMOR, new AttributeModifier(armorId, (double)this.getDefense(), AttributeModifier.Operation.ADD_VALUE));
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("alexscaves", "movement_speed"), speed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            Multimap<Holder<Attribute>, AttributeModifier> attributeModifierMultimap = builder.build();
            gingerbreadDurabilityDependentAttributes.put(durabilityIn, attributeModifierMultimap);
            return attributeModifierMultimap;
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        return equipmentSlot == this.type.getSlot() ? defaultAttributes : ImmutableMultimap.of();
    }

    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_0.png");
        }
    }
}
