package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.ArmorKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class DarknessArmorItem extends ArmorItem implements CustomArmorPostRender, KeybindUsingArmor, UpdatesStackTags {

    public DarknessArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties().rarity(ACItemRegistry.RARITY_DEMONIC));
    }

    private static boolean canChargeUp(LivingEntity entity, boolean creative) {
        return (!DarknessIncarnateEffect.isInLight(entity, 11) || creative && entity instanceof Player player && player.isCreative()) && entity.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.HOOD_OF_DARKNESS.get()) && !entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE);
    }

    public static boolean canChargeUp(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag == null || tag.getBoolean("CanCharge");
    }

    public static boolean hasMeter(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).is(ACItemRegistry.CLOAK_OF_DARKNESS.get()) && player.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.HOOD_OF_DARKNESS.get()) && !player.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE);
    }

    public static float getMeterProgress(ItemStack cloak) {
        CompoundTag tag = cloak.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag == null) {
            return 0.0F;
        } else {
            return tag.getInt("CloakCharge") / (float) AlexsCaves.COMMON_CONFIG.darknessCloakChargeTime.get();
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (stack.is(ACItemRegistry.CLOAK_OF_DARKNESS.get())) {
            if (!level.isClientSide) {
                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                int charge = tag.getInt("CloakCharge");
                boolean flag = false;
                if (charge < AlexsCaves.COMMON_CONFIG.darknessCloakChargeTime.get() && canChargeUp(stack)) {
                    charge += 1;
                    tag.putInt("CloakCharge", charge);
                    flag = true;
                }
                if (flag) {
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    AlexsCaves.sendNonLocal(new UpdateItemTagMessage(player.getId(), stack), (ServerPlayer) player);
                }
            }
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if (stack.is(ACItemRegistry.CLOAK_OF_DARKNESS.get()) && entity instanceof LivingEntity living) {
            if (living.getItemBySlot(EquipmentSlot.CHEST) == stack) {
                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if (!level.isClientSide) {
                    long lastLightTimestamp = tag.getLong("LastLightTimestamp");
                    long lastEquipMessageTime = tag.getLong("LastEquipMessageTime");
                    if (lastLightTimestamp <= 0 || level.getGameTime() - lastLightTimestamp > 10) {
                        tag.putLong("LastLightTimestamp", level.getGameTime());
                        tag.putBoolean("CanCharge", canChargeUp(living, true));
                    }
                    if (lastEquipMessageTime <= 0 && entity instanceof Player player && !level.isClientSide) {
                        tag.putLong("LastEquipMessageTime", level.getGameTime());
                        player.displayClientMessage(Component.translatable("item.alexscaves.cloak_of_darkness.equip"), true);
                    }
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                } else if (AlexsCaves.PROXY.getClientSidePlayer() == entity && getMeterProgress(stack) >= 1.0F && AlexsCaves.PROXY.isKeyDown(2)) {
                    AlexsCaves.sendMSGToServer(new ArmorKeyMessage(EquipmentSlot.CHEST.ordinal(), living.getId(), 2));
                    onKeyPacket(living, stack, 2);
                }
            }
        }
    }

    public void onKeyPacket(Entity wearer, ItemStack itemStack, int key) {
        if (wearer instanceof LivingEntity living && canChargeUp(living, false)) {
            CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt("CloakCharge", 0);
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            living.addEffect(new MobEffectInstance(ACEffectRegistry.DARKNESS_INCARNATE, AlexsCaves.COMMON_CONFIG.darknessCloakFlightTime.get(), 0, false, false, false));
        } else if (wearer instanceof Player player && !wearer.level().isClientSide) {
            player.displayClientMessage(Component.translatable("item.alexscaves.cloak_of_darkness.requires_darkness"), true);
        }
    }


    @Override
    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/darkness_armor.png");
    }
}
