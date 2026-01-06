package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BeholderSyncMessage(int beholderId, boolean active) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BeholderSyncMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "beholder_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeholderSyncMessage> CODEC = new StreamCodec<>() {
        @Override
        public BeholderSyncMessage decode(RegistryFriendlyByteBuf buf) {
            return new BeholderSyncMessage(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, BeholderSyncMessage packet) {
            buf.writeInt(packet.beholderId);
            buf.writeBoolean(packet.active);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(BeholderSyncMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Entity watcher = playerSided.level().getEntity(message.beholderId);
                if (watcher instanceof BeholderEyeEntity beholderEye) {
                    Entity beholderEyePlayer = beholderEye.getUsingPlayer();
                    beholderEye.hasTakenFullControlOfCamera = true;
                    if (beholderEyePlayer instanceof Player && beholderEyePlayer.equals(playerSided)) {
                        if (message.active) {
                            AlexsCaves.PROXY.setRenderViewEntity(playerSided, beholderEye);
                        } else {
                            AlexsCaves.PROXY.resetRenderViewEntity(playerSided);
                        }
                    }
                }
            }
        });
    }
}
