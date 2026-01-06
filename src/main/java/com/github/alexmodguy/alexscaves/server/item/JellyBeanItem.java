package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class JellyBeanItem extends Item {

    public JellyBeanItem() {
        super(new Item.Properties().food(ACFoods.JELLY_BEAN).stacksTo(16));
    }

    public static int getBeanColor(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            var customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.copyTag().getBoolean("Rainbow")) {
                float hue = (System.currentTimeMillis() % 4000) / 4000f;
                int rainbow = Color.HSBtoRGB(hue, 1f, 0.8f);
                return rainbow;
            }
        }
        PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null) {
            return potionContents.getColor();
        }
        return 0xFFFFFF;
    }

    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return 16;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int useDir) {
        Vec3 vec3 = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
        vec3 = vec3.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        vec3 = vec3.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        double d0 = (double) (-level.getRandom().nextFloat()) * 0.6D - 0.3D;
        Vec3 vec31 = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
        vec31 = vec31.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        vec31 = vec31.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        vec31 = vec31.add(living.getX(), living.getEyeY(), living.getZ());
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
        } else {
            level.addParticle(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null) {
            List<Pair<Holder<Attribute>, AttributeModifier>> list = Lists.newArrayList();

            for (MobEffectInstance mobeffectinstance : potionContents.getAllEffects()) {
                MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                Holder<MobEffect> mobeffect = mobeffectinstance.getEffect();

                if (mobeffectinstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                }

                if (!mobeffectinstance.endsWithin(20)) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, 1.0F, context.tickRate()));
                }

                tooltip.add(Component.translatable("item.alexscaves.jelly_bean.desc", mutablecomponent.withStyle(mobeffect.value().getCategory().getTooltipFormatting())).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        Player player = living instanceof Player ? (Player)living : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            if (potionContents != null) {
                for(MobEffectInstance mobeffectinstance : potionContents.getAllEffects()) {
                    if (mobeffectinstance.getEffect().value().isInstantenous()) {
                        mobeffectinstance.getEffect().value().applyInstantenousEffect(player, player, living, mobeffectinstance.getAmplifier(), 1.0D);
                    } else {
                        living.addEffect(new MobEffectInstance(mobeffectinstance));
                    }
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        living.gameEvent(GameEvent.EAT);
        return stack;
    }
}
