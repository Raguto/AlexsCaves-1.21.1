package com.github.alexmodguy.alexscaves.server.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class ACAdvancementTrigger extends SimpleCriterionTrigger<ACAdvancementTrigger.TriggerInstance> {
    
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public void triggerForEntity(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer);
        }
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
            ).apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> create(ACAdvancementTrigger trigger) {
            return trigger.createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
