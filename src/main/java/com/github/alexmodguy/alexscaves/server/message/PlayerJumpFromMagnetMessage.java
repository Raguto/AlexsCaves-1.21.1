package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerJumpFromMagnetMessage(int entityID, boolean jumping) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerJumpFromMagnetMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "player_jump_magnet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerJumpFromMagnetMessage> CODEC = new StreamCodec<>() {
        @Override
        public PlayerJumpFromMagnetMessage decode(RegistryFriendlyByteBuf buf) {
            return new PlayerJumpFromMagnetMessage(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayerJumpFromMagnetMessage packet) {
            buf.writeInt(packet.entityID);
            buf.writeBoolean(packet.jumping);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(PlayerJumpFromMagnetMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                Entity entity = player.level().getEntity(message.entityID);
                if (MagnetUtil.isPulledByMagnets(entity) && entity instanceof LivingEntity living) {
                    living.setJumping(message.jumping);
                }
            }
        });
    }
}
