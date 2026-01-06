package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MultipartEntityMessage(int parentId, int playerId, int interactionType, double damage) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MultipartEntityMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "multipart_entity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MultipartEntityMessage> CODEC = new StreamCodec<>() {
        @Override
        public MultipartEntityMessage decode(RegistryFriendlyByteBuf buf) {
            return new MultipartEntityMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readDouble());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MultipartEntityMessage packet) {
            buf.writeInt(packet.parentId);
            buf.writeInt(packet.playerId);
            buf.writeInt(packet.interactionType);
            buf.writeDouble(packet.damage);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(MultipartEntityMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null && playerSided.level() != null) {
                Entity parent = playerSided.level().getEntity(message.parentId);
                Entity interacter = playerSided.level().getEntity(message.playerId);
                if (interacter != null && parent != null && parent.isMultipartEntity() && interacter.distanceTo(parent) < 16) {
                    if (message.interactionType == 0) {
                        if (interacter instanceof Player player) {
                            parent.interact(player, player.getUsedItemHand());
                        }
                    } else if (message.interactionType == 1) {
                        parent.hurt(parent.damageSources().generic(), (float) message.damage);
                    }
                }
            }
        });
    }
}
