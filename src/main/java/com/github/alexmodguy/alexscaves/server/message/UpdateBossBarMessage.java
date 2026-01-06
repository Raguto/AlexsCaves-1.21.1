package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record UpdateBossBarMessage(UUID bossBar, int renderType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBossBarMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_boss_bar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBossBarMessage> CODEC = new StreamCodec<>() {
        @Override
        public UpdateBossBarMessage decode(RegistryFriendlyByteBuf buf) {
            return new UpdateBossBarMessage(buf.readUUID(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UpdateBossBarMessage packet) {
            buf.writeUUID(packet.bossBar);
            buf.writeInt(packet.renderType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(UpdateBossBarMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (message.renderType == -1) {
                AlexsCaves.PROXY.removeBossBarRender(message.bossBar);
            } else {
                AlexsCaves.PROXY.setBossBarRender(message.bossBar, message.renderType);
            }
        });
    }
}
