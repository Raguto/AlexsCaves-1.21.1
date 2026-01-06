package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpelunkeryTableCompleteTutorialMessage(boolean completedTutorial) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpelunkeryTableCompleteTutorialMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "spelunkery_tutorial_complete"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpelunkeryTableCompleteTutorialMessage> CODEC = new StreamCodec<>() {
        @Override
        public SpelunkeryTableCompleteTutorialMessage decode(RegistryFriendlyByteBuf buf) {
            return new SpelunkeryTableCompleteTutorialMessage(buf.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SpelunkeryTableCompleteTutorialMessage packet) {
            buf.writeBoolean(packet.completedTutorial);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(SpelunkeryTableCompleteTutorialMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            AlexsCaves.PROXY.setSpelunkeryTutorialComplete(message.completedTutorial);
        });
    }
}
