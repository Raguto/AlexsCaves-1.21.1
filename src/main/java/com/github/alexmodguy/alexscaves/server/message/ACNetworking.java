package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles network packet registration for Alex's Caves.
 * Register this in the main mod class with: modEventBus.addListener(ACNetworking::register);
 */
public class ACNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // Bidirectional packets
        registrar.playBidirectional(
            ArmorKeyMessage.ID,
            ArmorKeyMessage.CODEC,
            ArmorKeyMessage::handle
        );
        registrar.playBidirectional(
            MultipartEntityMessage.ID,
            MultipartEntityMessage.CODEC,
            MultipartEntityMessage::handle
        );
        registrar.playBidirectional(
            UpdateEffectVisualityEntityMessage.ID,
            UpdateEffectVisualityEntityMessage.CODEC,
            UpdateEffectVisualityEntityMessage::handle
        );

        // Client -> Server packets
        registrar.playToServer(
            SpelunkeryTableChangeMessage.ID,
            SpelunkeryTableChangeMessage.CODEC,
            SpelunkeryTableChangeMessage::handleServer
        );
        registrar.playToClient(
            SpelunkeryTableCompleteTutorialMessage.ID,
            SpelunkeryTableCompleteTutorialMessage.CODEC,
            SpelunkeryTableCompleteTutorialMessage::handleClient
        );
        registrar.playToServer(
            PlayerJumpFromMagnetMessage.ID,
            PlayerJumpFromMagnetMessage.CODEC,
            PlayerJumpFromMagnetMessage::handleServer
        );
        registrar.playToServer(
            MountedEntityKeyMessage.ID,
            MountedEntityKeyMessage.CODEC,
            MountedEntityKeyMessage::handleServer
        );
        registrar.playToServer(
            PossessionKeyMessage.ID,
            PossessionKeyMessage.CODEC,
            PossessionKeyMessage::handleServer
        );
        registrar.playToServer(
            BeholderRotateMessage.ID,
            BeholderRotateMessage.CODEC,
            BeholderRotateMessage::handleServer
        );

        // Server -> Client packets
        registrar.playToClient(
            UpdateItemTagMessage.ID,
            UpdateItemTagMessage.CODEC,
            UpdateItemTagMessage::handleClient
        );
        registrar.playToClient(
            BeholderSyncMessage.ID,
            BeholderSyncMessage.CODEC,
            BeholderSyncMessage::handleClient
        );
        registrar.playToClient(
            WorldEventMessage.ID,
            WorldEventMessage.CODEC,
            WorldEventMessage::handleClient
        );
        registrar.playToClient(
            UpdateCaveBiomeMapTagMessage.ID,
            UpdateCaveBiomeMapTagMessage.CODEC,
            UpdateCaveBiomeMapTagMessage::handleClient
        );
        registrar.playToClient(
            UpdateBossEruptionStatus.ID,
            UpdateBossEruptionStatus.CODEC,
            UpdateBossEruptionStatus::handleClient
        );
        registrar.playToClient(
            UpdateBossBarMessage.ID,
            UpdateBossBarMessage.CODEC,
            UpdateBossBarMessage::handleClient
        );
        registrar.playToClient(
            SundropRainbowMessage.ID,
            SundropRainbowMessage.CODEC,
            SundropRainbowMessage::handleClient
        );
    }
}
