package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record UpdateCaveBiomeMapTagMessage(UUID userUUID, UUID caveBiomeMapUUID, CompoundTag tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateCaveBiomeMapTagMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_cave_biome_map"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCaveBiomeMapTagMessage> CODEC = new StreamCodec<>() {
        @Override
        public UpdateCaveBiomeMapTagMessage decode(RegistryFriendlyByteBuf buf) {
            return new UpdateCaveBiomeMapTagMessage(buf.readUUID(), buf.readUUID(), buf.readNbt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, UpdateCaveBiomeMapTagMessage packet) {
            buf.writeUUID(packet.userUUID);
            buf.writeUUID(packet.caveBiomeMapUUID);
            buf.writeNbt(packet.tag);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(UpdateCaveBiomeMapTagMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Player player = playerSided.level().getPlayerByUUID(message.userUUID);
                if (player != null) {
                    ItemStack set = null;
                    for (int i = 0; i < player.getInventory().items.size(); i++) {
                        ItemStack itemStack = player.getInventory().items.get(i);
                        if (itemStack.is(ACItemRegistry.CAVE_MAP.get())) {
                            // TODO 1.21: NBT tags replaced with DataComponents - needs conversion
                            // This needs to be updated to use DataComponents instead of getTag()
                            set = itemStack;
                            break;
                        }
                    }
                    if (set != null) {
                        // TODO 1.21: set.setTag(message.tag) - needs DataComponent conversion
                    }
                }
            }
        });
    }
}
