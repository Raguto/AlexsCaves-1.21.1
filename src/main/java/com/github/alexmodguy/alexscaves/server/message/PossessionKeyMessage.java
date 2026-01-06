package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PossessionKeyMessage(int watcher, int playerId, int keyType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PossessionKeyMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "possession_key"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PossessionKeyMessage> CODEC = new StreamCodec<>() {
        @Override
        public PossessionKeyMessage decode(RegistryFriendlyByteBuf buf) {
            return new PossessionKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PossessionKeyMessage packet) {
            buf.writeInt(packet.watcher);
            buf.writeInt(packet.playerId);
            buf.writeInt(packet.keyType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(PossessionKeyMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Entity watcher = playerSided.level().getEntity(message.watcher);
                Entity keyPresser = playerSided.level().getEntity(message.playerId);
                if (watcher instanceof PossessesCamera watcherEntity && keyPresser instanceof Player) {
                    watcherEntity.onPossessionKeyPacket(keyPresser, message.keyType);
                }
            }
        });
    }
}
