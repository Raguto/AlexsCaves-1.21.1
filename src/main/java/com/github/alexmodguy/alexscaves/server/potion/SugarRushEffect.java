package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.LocalEntityTickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SugarRushEffect extends MobEffect {


    protected SugarRushEffect() {
        super(MobEffectCategory.BENEFICIAL, 0XFFA4EB);
    this.addAttributeModifier(
        Objects.requireNonNull(Attributes.MOVEMENT_SPEED, "movementSpeed"),
        Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "sugar_rush_speed"), "sugarRushSpeedId"),
        0.25F,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    public boolean applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        if(entity.level().isClientSide){
            AlexsCaves.PROXY.playWorldSound(entity, (byte)18);
        } else {
            var motion = Objects.requireNonNull(entity.getDeltaMovement(), "deltaMovement");
            double verticalSpeed = motion.y;
            if (verticalSpeed > 0) {
                entity.setDeltaMovement(Objects.requireNonNull(motion.multiply(1.0D, 0.85D, 1.0D), "deltaMovementUp"));
            } else if (verticalSpeed < 0) {
                entity.setDeltaMovement(Objects.requireNonNull(motion.multiply(1.0D, 0.45D, 1.0D), "deltaMovementDown"));
                var slowFalling = Objects.requireNonNull(MobEffects.SLOW_FALLING, "slowFalling");
                if (!entity.hasEffect(slowFalling)) {
                    entity.addEffect(new MobEffectInstance(slowFalling, 10, 0, false, false, false));
                }
            }
        }
        return true;
    }

    public static void enterSlowMotion(Player entity, Level level, int duration, float speed) {
        if (!level.isClientSide && level instanceof ServerLevel) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(level.getServer());
            for (TickRateModifier modifier : tracker.tickRateModifierList) {
                if (modifier instanceof LocalEntityTickRateModifier entityTick && entityTick.getEntityId() == entity.getId()) {
                    modifier.setMaxDuration(duration);
                    return;
                }
            }
            tracker.addTickRateModifier(new LocalEntityTickRateModifier(entity.getId(), entity.getType(), 10, level.dimension(), duration, speed));
        }
    }

    public static void leaveSlowMotion(Player entity, Level level) {
        if (!level.isClientSide && level instanceof ServerLevel) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(level.getServer());
            TickRateModifier toRemove = null;
            for (TickRateModifier modifier : tracker.tickRateModifierList) {
                if (modifier instanceof LocalEntityTickRateModifier entityTick && entityTick.getEntityId() == entity.getId()) {
                    toRemove = modifier;
                }
            }
            if (toRemove != null) {
                tracker.tickRateModifierList.remove(toRemove);
            }
        }
    }

}
