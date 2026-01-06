package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WorldEventMessage(int messageId, int blockX, int blockY, int blockZ) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WorldEventMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "world_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WorldEventMessage> CODEC = new StreamCodec<>() {
        @Override
        public WorldEventMessage decode(RegistryFriendlyByteBuf buf) {
            return new WorldEventMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, WorldEventMessage packet) {
            buf.writeInt(packet.messageId);
            buf.writeInt(packet.blockX);
            buf.writeInt(packet.blockY);
            buf.writeInt(packet.blockZ);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(WorldEventMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null && playerSided.level() != null) {
                BlockPos blockPos = new BlockPos(message.blockX, message.blockY, message.blockZ);
                AlexsCaves.PROXY.playWorldEvent(message.messageId, playerSided.level(), blockPos);
            }
        });
    }
}
