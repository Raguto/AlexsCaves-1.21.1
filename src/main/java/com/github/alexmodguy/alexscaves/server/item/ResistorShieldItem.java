package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;

public class ResistorShieldItem extends ShieldItem {

    public ResistorShieldItem() {
        super(new Item.Properties().stacksTo(1).durability(1000).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        if (player.isShiftKeyDown()) {
            setPolarity(itemstack, !isScarlet(itemstack));
        }
        player.playSound(ACSoundRegistry.RESITOR_SHIELD_SPIN.get());
        return InteractionResultHolder.consume(itemstack);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.alexscaves.resistor_shield.desc").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        super.onUseTick(level, living, stack, timeUsing);
        int i = getUseDuration(stack, living) - timeUsing;
        boolean scarlet = isScarlet(stack);
        boolean firstHit = i >= 10 && i <= 12;
        int slamEnchantAmount = 0;
        if(living instanceof Player player){
            slamEnchantAmount = stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.HEAVY_SLAM));
        }
        float range = 5F;
        if (level.isClientSide) {
            setUseTime(stack, i);
            if(i == 10){
                living.playSound(ACSoundRegistry.RESITOR_SHIELD_SLAM.get());
            }
            if (i >= 10 && i % 5 == 0) {
                AlexsCaves.PROXY.playWorldSound(living, (byte) (scarlet ? 9 : 10));
                Vec3 particlesFrom = living.position().add(0, 0.2, 0);
                float particleMax = 5 + living.getRandom().nextInt(5);
                for (int particles = 0; particles < particleMax; particles++) {
                    Vec3 vec3 = new Vec3((living.getRandom().nextFloat() - 0.5) * 0.3F, (living.getRandom().nextFloat() - 0.5) * 0.3F, range * 0.5F + range * 0.5F * living.getRandom().nextFloat()).yRot((float) ((particles / particleMax) * Math.PI * 2)).add(particlesFrom);
                    if (scarlet) {
                        level.addParticle(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING.get(), vec3.x, vec3.y, vec3.z, particlesFrom.x, particlesFrom.y, particlesFrom.z);
                    } else {
                        level.addParticle(ACParticleRegistry.AZURE_SHIELD_LIGHTNING.get(), particlesFrom.x, particlesFrom.y, particlesFrom.z, vec3.x, vec3.y, vec3.z);
                    }
                }
            }
        }
        if (i >= 10 && i % 5 == 0) {
            AABB bashBox = living.getBoundingBox().inflate(5, 1, 5);
            for (LivingEntity entity : living.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
                if (!living.isAlliedTo(entity) && !entity.equals(living) && entity.distanceTo(living) <= range) {
                    entity.hurt(living.damageSources().mobAttack(living), firstHit ? 6 + (slamEnchantAmount * 3) : 2);
                    if (scarlet) {
                        entity.knockback(firstHit ? 0.5D : 0.2D, entity.getX() - living.getX(), entity.getZ() - living.getZ());
                    } else {
                        entity.knockback(firstHit ? 0.5D : 0.2D, living.getX() - living.getX(), living.getZ() - entity.getZ());
                    }
                }
            }
        }
        if (i == 10 && !level.isClientSide) {
            stack.hurtAndBreak(1, living, living.getUsedItemHand() == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack repairItem) {
        return repairItem.is(ACItemRegistry.SCARLET_NEODYMIUM_INGOT.get()) || repairItem.is(ACItemRegistry.AZURE_NEODYMIUM_INGOT.get()) || super.isValidRepairItem(item, repairItem);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity player, int useTimeLeft) {
        super.releaseUsing(stack, level, player, useTimeLeft);
        AlexsCaves.PROXY.clearSoundCacheFor(player);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if (getUseTime(stack) != 0 && entity instanceof LivingEntity living && !living.getUseItem().equals(stack)) {
            setUseTime(stack, 0);
            CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            tag.putInt("PrevUseTime", 0);
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        }
        if (level.isClientSide) {
            boolean scarlet = isScarlet(stack);
            int switchTime = getSwitchTime(stack);
            CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            if (tag.getInt("PrevSwitchTime") != tag.getInt("SwitchTime")) {
                tag.putInt("PrevSwitchTime", getSwitchTime(stack));
                stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            }
            if (scarlet && switchTime < 5.0F) {
                setSwitchTime(stack, switchTime + 1);
            }
            if (!scarlet && switchTime > 0.0F) {
                setSwitchTime(stack, switchTime - 1);
            }
        }
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        tag.putInt("PrevUseTime", getUseTime(stack));
        tag.putInt("UseTime", useTime);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }

    public static void setSwitchTime(ItemStack stack, int useTime) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        tag.putInt("PrevSwitchTime", getSwitchTime(stack));
        tag.putInt("SwitchTime", useTime);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }


    public static void setPolarity(ItemStack stack, boolean scarlet) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        tag.putBoolean("Polarity", scarlet);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        return !compoundtag.isEmpty() ? compoundtag.getInt("UseTime") : 0;
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        float prev = !compoundtag.isEmpty() ? (float) compoundtag.getInt("PrevUseTime") : 0F;
        float current = !compoundtag.isEmpty() ? (float) compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    public static int getSwitchTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        return !compoundtag.isEmpty() ? compoundtag.getInt("SwitchTime") : 0;
    }

    public static float getLerpedSwitchTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        float prev = !compoundtag.isEmpty() ? (float) compoundtag.getInt("PrevSwitchTime") : 0F;
        float current = !compoundtag.isEmpty() ? (float) compoundtag.getInt("SwitchTime") : 0F;
        return prev + f * (current - prev);
    }

    public static boolean isScarlet(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        return !compoundtag.isEmpty() ? compoundtag.getBoolean("Polarity") : false;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(ACItemRegistry.RESISTOR_SHIELD.get()) || !newStack.is(ACItemRegistry.RESISTOR_SHIELD.get());
    }
}
