package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateBossEruptionStatus(int entityId, boolean erupting) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBossEruptionStatus> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_boss_eruption"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBossEruptionStatus> CODEC = new StreamCodec<>() {
        @Override
        public UpdateBossEruptionStatus decode(RegistryFriendlyByteBuf buf) {
            return new UpdateBossEruptionStatus(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UpdateBossEruptionStatus packet) {
            buf.writeInt(packet.entityId);
            buf.writeBoolean(packet.erupting);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(UpdateBossEruptionStatus message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                AlexsCaves.PROXY.setPrimordialBossActive(playerSided.level(), message.entityId, message.erupting);
            }
        });
    }
}
