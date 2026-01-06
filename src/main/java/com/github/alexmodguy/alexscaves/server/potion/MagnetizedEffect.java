package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class MagnetizedEffect extends MobEffect {

    protected MagnetizedEffect() {
        super(MobEffectCategory.NEUTRAL, 0X53556C);
    }

    public boolean applyEffectTick(LivingEntity entity, int tick) {
        if (!entity.level().isClientSide && entity.tickCount % 20 == 0) {
            MobEffectInstance instance = entity.getEffect(ACEffectRegistry.MAGNETIZING);
            if (instance != null) {
                AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 2, instance.getDuration()));
            }
        }
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        if (!entity.level().isClientSide) {
            MobEffectInstance instance = entity.getEffect(ACEffectRegistry.MAGNETIZING);
            if (instance != null) {
                AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 2, instance.getDuration()));
            }
        }
    }

}
