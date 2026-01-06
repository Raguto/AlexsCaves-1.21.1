package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * In 1.21, ArmorMaterial is a record that must be registered.
 * This class provides static Holder references to our armor materials.
 */
public final class ACArmorMaterial {
    
    public static final Holder<ArmorMaterial> PRIMORDIAL = register("primordial", 
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 3);
            map.put(ArmorItem.Type.CHESTPLATE, 4);
            map.put(ArmorItem.Type.HELMET, 3);
            map.put(ArmorItem.Type.BODY, 4);
        }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY);
    
    public static final Holder<ArmorMaterial> HAZMAT_SUIT = register("hazmat_suit",
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 4);
            map.put(ArmorItem.Type.HELMET, 2);
            map.put(ArmorItem.Type.BODY, 5);
        }), 25, SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.0F, () -> Ingredient.EMPTY);
    
    public static final Holder<ArmorMaterial> DIVING_SUIT = register("diving_suit",
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 6);
            map.put(ArmorItem.Type.HELMET, 2);
            map.put(ArmorItem.Type.BODY, 6);
        }), 25, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.EMPTY);
    
    public static final Holder<ArmorMaterial> DARKNESS = register("darkness",
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 1);
            map.put(ArmorItem.Type.LEGGINGS, 1);
            map.put(ArmorItem.Type.CHESTPLATE, 5);
            map.put(ArmorItem.Type.HELMET, 4);
            map.put(ArmorItem.Type.BODY, 5);
        }), 40, SoundEvents.ARMOR_EQUIP_LEATHER, 0.5F, 0.0F, () -> Ingredient.EMPTY);
    
    public static final Holder<ArmorMaterial> RAINBOUNCE = register("rainbounce",
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 1);
            map.put(ArmorItem.Type.CHESTPLATE, 2);
            map.put(ArmorItem.Type.HELMET, 2);
            map.put(ArmorItem.Type.BODY, 2);
        }), 40, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.EMPTY);
    
    public static final Holder<ArmorMaterial> GINGERBREAD = register("gingerbread",
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 4);
            map.put(ArmorItem.Type.HELMET, 2);
            map.put(ArmorItem.Type.BODY, 4);
        }), 25, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY);

    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, 
            int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, name);
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));
        
        ArmorMaterial material = new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, 
            layers, toughness, knockbackResistance);
        
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location, material);
    }
    
    // Private constructor to prevent instantiation
    private ACArmorMaterial() {}
    
    public static void init() {
        // Called to ensure static initialization
    }
}
