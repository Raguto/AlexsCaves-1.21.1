package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.KeybindUsingArmor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArmorKeyMessage(int equipmentSlot, int playerId, int keyType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ArmorKeyMessage> ID =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "armor_key"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorKeyMessage> CODEC = new StreamCodec<>() {
        @Override
        public ArmorKeyMessage decode(RegistryFriendlyByteBuf buf) {
            return new ArmorKeyMessage(buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ArmorKeyMessage packet) {
            buf.writeInt(packet.equipmentSlot);
            buf.writeInt(packet.playerId);
            buf.writeInt(packet.keyType);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(ArmorKeyMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerSided = context.player();
            if (playerSided != null) {
                Entity keyPresser = playerSided.level().getEntity(message.playerId);
                EquipmentSlot equipmentSlot1 = EquipmentSlot.values()[Mth.clamp(message.equipmentSlot, 0, EquipmentSlot.values().length - 1)];
                if (keyPresser instanceof Player player) {
                    ItemStack stack = player.getItemBySlot(equipmentSlot1);
                    if (stack.getItem() instanceof KeybindUsingArmor armor) {
                        armor.onKeyPacket(keyPresser, stack, message.keyType);
                    }
                }
            }
        });
    }
}
