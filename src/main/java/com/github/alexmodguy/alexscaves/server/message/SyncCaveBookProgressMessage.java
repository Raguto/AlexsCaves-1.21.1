package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.CaveBookProgress;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Syncs the player's cave book progress from server to client.
 * This packet ensures that when a player uses a Cave Codex, the client-side
 * cave compendium is immediately updated instead of waiting for a game restart.
 */
public record SyncCaveBookProgressMessage(CompoundTag caveBookProgressTag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncCaveBookProgressMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "sync_cave_book_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCaveBookProgressMessage> CODEC = new StreamCodec<>() {
        @Override
        public SyncCaveBookProgressMessage decode(RegistryFriendlyByteBuf buf) {
            return new SyncCaveBookProgressMessage(buf.readNbt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncCaveBookProgressMessage packet) {
            buf.writeNbt(packet.caveBookProgressTag);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(SyncCaveBookProgressMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                // Update the client-side player's cave book progress
                CompoundTag citadelTag = CitadelEntityData.getOrCreateCitadelTag(playerSided);
                citadelTag.put(CaveBookProgress.PLAYER_CAVE_BOOK_PROGRESS_TAG, message.caveBookProgressTag);
                CitadelEntityData.setCitadelTag(playerSided, citadelTag);
            }
        });
    }
}
