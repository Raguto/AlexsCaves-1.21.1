package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MountedEntityKeyMessage(int mountId, int playerId, int keyType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MountedEntityKeyMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "mounted_entity_key"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MountedEntityKeyMessage> CODEC = new StreamCodec<>() {
        @Override
        public MountedEntityKeyMessage decode(RegistryFriendlyByteBuf buf) {
            return new MountedEntityKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MountedEntityKeyMessage packet) {
            buf.writeInt(packet.mountId);
            buf.writeInt(packet.playerId);
            buf.writeInt(packet.keyType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(MountedEntityKeyMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Entity parent = playerSided.level().getEntity(message.mountId);
                Entity keyPresser = playerSided.level().getEntity(message.playerId);
                if (keyPresser != null && parent instanceof KeybindUsingMount mount && keyPresser instanceof Player && keyPresser.isPassengerOfSameVehicle(parent)) {
                    mount.onKeyPacket(keyPresser, message.keyType);
                }
            }
        });
    }
}
