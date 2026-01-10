package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.event.CommonEvents;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.carver.ACCarverRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ACStructurePieceRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRuleConditionRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRules;
import com.github.alexmodguy.alexscaves.server.misc.*;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.recipe.ACRecipeRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(AlexsCaves.MODID)
public class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CommonProxy PROXY = unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static final List<String> MOD_GENERATION_CONFLICTS = new ArrayList<>();
    
    public static final ACServerConfig COMMON_CONFIG = ACServerConfig.INSTANCE;
    public static final ACClientConfig CLIENT_CONFIG = ACClientConfig.INSTANCE;
    public static final TicketController TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(MODID, "chunk_loader"), ACWorldData::clearLoadedChunksCallback);

    public AlexsCaves(ModContainer modContainer, IEventBus modEventBus) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ACServerConfig.SPEC, "alexscaves-general.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ACClientConfig.SPEC, "alexscaves-client.toml");
        
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::loadConfig);
        modEventBus.addListener(this::reloadConfig);
        modEventBus.addListener(this::registerLayerDefinitions);
        modEventBus.addListener(this::registerTicketControllers);
        
        ACSoundRegistry.DEF_REG.register(modEventBus);
        ACBlockRegistry.DEF_REG.register(modEventBus);
        ACBlockEntityRegistry.DEF_REG.register(modEventBus);
        ACItemRegistry.DEF_REG.register(modEventBus);
        ACParticleRegistry.DEF_REG.register(modEventBus);
        ACEntityRegistry.DEF_REG.register(modEventBus);
        ACEntityDataRegistry.DEF_REG.register(modEventBus);
        ACPOIRegistry.DEF_REG.register(modEventBus);
        ACFeatureRegistry.DEF_REG.register(modEventBus);
        ACSurfaceRuleConditionRegistry.DEF_REG.register(modEventBus);
        ACCarverRegistry.DEF_REG.register(modEventBus);
        ACStructureRegistry.DEF_REG.register(modEventBus);
        ACStructurePieceRegistry.DEF_REG.register(modEventBus);
        ACStructureProcessorRegistry.DEF_REG.register(modEventBus);
        ACEffectRegistry.DEF_REG.register(modEventBus);
        ACEffectRegistry.POTION_DEF_REG.register(modEventBus);
        ACMenuRegistry.DEF_REG.register(modEventBus);
        ACRecipeRegistry.DEF_REG.register(modEventBus);
        ACRecipeRegistry.TYPE_DEF_REG.register(modEventBus);
        ACFrogRegistry.DEF_REG.register(modEventBus);
        ACFluidRegistry.FLUID_TYPE_DEF_REG.register(modEventBus);
        ACFluidRegistry.FLUID_DEF_REG.register(modEventBus);
        ACLootTableRegistry.GLOBAL_LOOT_MODIFIER_DEF_REG.register(modEventBus);
        ACLootTableRegistry.LOOT_FUNCTION_DEF_REG.register(modEventBus);
        ACCreativeTabRegistry.DEF_REG.register(modEventBus);
        ACPotPatternRegistry.DEF_REG.register(modEventBus);
        ACAdvancementTriggerRegistry.DEF_REG.register(modEventBus);
        
        modEventBus.addListener(com.github.alexmodguy.alexscaves.server.message.ACNetworking::register);
        
        PROXY.setModEventBus(modEventBus);
        NeoForge.EVENT_BUS.register(new CommonEvents());//FMLEnvironment thinks that this is the client side. I advise you to abandon proxies in the future.
        ACBiomeRegistry.init();
    }

    private void loadConfig(final ModConfigEvent.Loading event) {
        BiomeGenerationConfig.reloadConfig();
    }

    private void reloadConfig(final ModConfigEvent.Reloading event) {
        BiomeGenerationConfig.reloadConfig();
    }
    
    private void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(TICKET_CONTROLLER);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PROXY.initPathfinding();
        event.enqueueWork(() -> {
            ACSurfaceRules.setup();
            ACPlayerCapes.setup();
            ACEffectRegistry.setup();
            ACBlockRegistry.setup();
            ACItemRegistry.setup();
            ACPotPatternRegistry.expandVanillaDefinitions();
            ACBlockEntityRegistry.expandVanillaDefinitions();
        });
        readModIncompatibilities();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientInit());
    }

    public static void sendMSGToServer(net.minecraft.network.protocol.common.custom.CustomPacketPayload message) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(message);
    }

    public static void sendMSGToAll(net.minecraft.network.protocol.common.custom.CustomPacketPayload message) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, message);
            }
        }
    }

    public static void sendNonLocal(net.minecraft.network.protocol.common.custom.CustomPacketPayload msg, ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, msg);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(ACFluidRegistry::postInit);
        event.enqueueWork(ACLoadedMods::afterAllModsLoaded);
    }

    private void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        ACModelLayers.register(event);
    }

    private void readModIncompatibilities() {
        BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/AlexModGuy/AlexsCaves/main/src/main/resources/assets/alexscaves/warning/mod_generation_conflicts.txt", "assets/alexscaves/warning/mod_generation_conflicts.txt");
        if (urlContents != null) {
            try {
                String line;
                while ((line = urlContents.readLine()) != null) {
                    MOD_GENERATION_CONFLICTS.add(line);
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to load mod conflicts");
            }
        } else {
            LOGGER.warn("Failed to load mod conflicts");
        }
    }
    
    private static <T> T unsafeRunForDist(java.util.function.Supplier<java.util.function.Supplier<T>> clientTarget, java.util.function.Supplier<java.util.function.Supplier<T>> serverTarget) {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }
}
