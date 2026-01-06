package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public record BeholderRotateMessage(int beholderId, float rotX, float rotY) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeholderRotateMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_rotate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeholderRotateMessage> CODEC = new StreamCodec<>() {
        @Override
        public BeholderRotateMessage decode(RegistryFriendlyByteBuf buf) {
            return new BeholderRotateMessage(buf.readInt(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, BeholderRotateMessage packet) {
            buf.writeInt(packet.beholderId);
            buf.writeFloat(packet.rotX);
            buf.writeFloat(packet.rotY);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(BeholderRotateMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null && ServerLifecycleHooks.getCurrentServer() != null) {
                Level serverLevel = ServerLifecycleHooks.getCurrentServer().getLevel(playerSided.level().dimension());
                if (serverLevel != null) {
                    Entity watcher = serverLevel.getEntity(message.beholderId);
                    if (watcher instanceof BeholderEyeEntity beholderEye) {
                        // Original code was empty here - placeholder for rotation logic
                    }
                }
            }
        });
    }
}
