package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientLayerRegistry {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
                BuiltInRegistries.ENTITY_TYPE.stream()
                        .filter(DefaultAttributes::hasSupplier)
                        .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                        .collect(Collectors.toList()));
        entityTypes.forEach((entityType -> {
            addLayerIfApplicable(entityType, event);
        }));
        for (var skinModel : event.getSkins()) {
            var skinRenderer = event.getSkin(skinModel);
            if (skinRenderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new ACPotionEffectLayer(livingRenderer));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> void addLayerIfApplicable(EntityType<T> entityType, EntityRenderersEvent.AddLayers event) {
        LivingEntityRenderer<T, ?> renderer = null;
        if (entityType != EntityType.ENDER_DRAGON) {
            try {
                renderer = (LivingEntityRenderer<T, ?>) event.getRenderer(entityType);
            } catch (Exception e) {
                AlexsCaves.LOGGER.warn("Could not apply radiation glow layer to " + BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + ", has custom renderer that is not LivingEntityRenderer.");
            }
            if (renderer != null) {
                renderer.addLayer(new ACPotionEffectLayer(renderer));
            }
        }
    }
}
