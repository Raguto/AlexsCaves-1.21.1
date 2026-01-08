package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.UpdatesStackTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateItemTagMessage(int entityId, ItemStack itemStackFrom) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateItemTagMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_item_tag"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateItemTagMessage> CODEC = new StreamCodec<>() {
        @Override
        public UpdateItemTagMessage decode(RegistryFriendlyByteBuf buf) {
            return new UpdateItemTagMessage(buf.readInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UpdateItemTagMessage packet) {
            buf.writeInt(packet.entityId);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.itemStackFrom);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(UpdateItemTagMessage message, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            handleClient(message, context);
        } else {
            handleServer(message, context);
        }
    }

    public static void handleClient(UpdateItemTagMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Entity holder = playerSided.level().getEntity(message.entityId);
                if (holder instanceof LivingEntity living) {
                    ItemStack stackFrom = message.itemStackFrom;
                    ItemStack to = null;
                    if (living.getItemInHand(InteractionHand.MAIN_HAND).is(stackFrom.getItem())) {
                        to = living.getItemInHand(InteractionHand.MAIN_HAND);
                    } else if (living.getItemInHand(InteractionHand.OFF_HAND).is(stackFrom.getItem())) {
                        to = living.getItemInHand(InteractionHand.OFF_HAND);
                    }
                    if (to != null && to.getItem() instanceof UpdatesStackTags updatesStackTags) {
                        net.minecraft.nbt.CompoundTag tag = stackFrom.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
                        updatesStackTags.updateTagFromServer(holder, to, tag);
                    }
                }
            }
        });
    }

    public static void handleServer(UpdateItemTagMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                // Server receives the updated item tag from client
                // This is used by items like the raygun to sync their state when stopping use
                ItemStack stackFrom = message.itemStackFrom;
                ItemStack to = null;
                if (player.getItemInHand(InteractionHand.MAIN_HAND).is(stackFrom.getItem())) {
                    to = player.getItemInHand(InteractionHand.MAIN_HAND);
                } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(stackFrom.getItem())) {
                    to = player.getItemInHand(InteractionHand.OFF_HAND);
                }
                if (to != null && to.getItem() instanceof UpdatesStackTags updatesStackTags) {
                    net.minecraft.nbt.CompoundTag tag = stackFrom.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
                    updatesStackTags.updateTagFromServer(player, to, tag);
                }
            }
        });
    }
}
