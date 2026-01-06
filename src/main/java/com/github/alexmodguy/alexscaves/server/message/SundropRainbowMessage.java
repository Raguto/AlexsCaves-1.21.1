package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SundropRainbowMessage(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SundropRainbowMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "sundrop_rainbow"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SundropRainbowMessage> CODEC = new StreamCodec<>() {
        @Override
        public SundropRainbowMessage decode(RegistryFriendlyByteBuf buf) {
            return new SundropRainbowMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SundropRainbowMessage packet) {
            buf.writeInt(packet.fromX);
            buf.writeInt(packet.fromY);
            buf.writeInt(packet.fromZ);
            buf.writeInt(packet.toX);
            buf.writeInt(packet.toY);
            buf.writeInt(packet.toZ);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(SundropRainbowMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null && playerSided.level() != null) {
                BlockPos blockPos1 = new BlockPos(message.fromX, message.fromY, message.fromZ);
                BlockPos blockPos2 = new BlockPos(message.toX, message.toY, message.toZ);
                if (playerSided.level().hasChunkAt(blockPos1) && playerSided.level().getBlockState(blockPos1).is(ACBlockRegistry.SUNDROP.get())) {
                    playerSided.level().addAlwaysVisibleParticle(ACParticleRegistry.RAINBOW.get(), true, blockPos1.getX() + 0.5F, blockPos1.getY() + 0.5F, blockPos1.getZ() + 0.5F, blockPos2.getX() + 0.5F, blockPos2.getY() + 0.5F, blockPos2.getZ() + 0.5F);
                }
            }
        });
    }
}
