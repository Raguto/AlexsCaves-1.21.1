package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HolocoderItem extends Item {
    public HolocoderItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof ArmorStand || !target.isAlive()) {
            return InteractionResult.PASS;
        }
        
        // Create the holocoder data
        CompoundTag tag = new CompoundTag();
        tag.putUUID("BoundEntityUUID", target.getUUID());
        
        // Serialize entity data - for players, just store UUID and id
        CompoundTag entityTag = target instanceof Player ? new CompoundTag() : target.serializeNBT(player.level().registryAccess());
        entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
        if (target instanceof Player) {
            entityTag.putUUID("UUID", target.getUUID());
        }
        tag.put("BoundEntityTag", entityTag);
        
        // Create new holocoder with bound entity
        ItemStack newHolocoder = new ItemStack(ACItemRegistry.HOLOCODER.get());
        newHolocoder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        
        // Shrink original stack
        stack.shrink(1);
        
        // Give new holocoder to player
        player.swing(hand);
        if (!player.addItem(newHolocoder)) {
            ItemEntity itemEntity = player.drop(newHolocoder, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setThrower(player);
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.isEmpty()) {
            Tag entity = tag.get("BoundEntityTag");
            if (entity instanceof CompoundTag) {
                Optional<EntityType<?>> optional = EntityType.by((CompoundTag) entity);
                if (optional.isPresent()) {
                    Component untranslated = optional.get().getDescription().copy().withStyle(ChatFormatting.GRAY);
                    tooltip.add(untranslated);
                }
            }
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    public static UUID getBoundEntityUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.isEmpty() && tag.contains("BoundEntityUUID")) {
            return tag.getUUID("BoundEntityUUID");
        } else {
            return null;
        }
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundEntityUUID(stack) != null;
    }
}
