package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.inventory.SpelunkeryTableMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpelunkeryTableChangeMessage(boolean pass) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpelunkeryTableChangeMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "spelunkery_table_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpelunkeryTableChangeMessage> CODEC = new StreamCodec<>() {
        @Override
        public SpelunkeryTableChangeMessage decode(RegistryFriendlyByteBuf buf) {
            return new SpelunkeryTableChangeMessage(buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SpelunkeryTableChangeMessage packet) {
            buf.writeBoolean(packet.pass);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleServer(SpelunkeryTableChangeMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                if (player.containerMenu instanceof SpelunkeryTableMenu tableMenu) {
                    tableMenu.onMessageFromScreen(player, message.pass);
                }
            }
        });
    }
}
