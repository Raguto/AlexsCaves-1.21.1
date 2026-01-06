package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PrimitiveClubItem extends Item {
    private static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_damage");
    private static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_speed");
    private final Multimap<Holder<Attribute>, AttributeModifier>[] defaultModifiers = new ImmutableMultimap[4];

    public PrimitiveClubItem(Item.Properties properties) {
        super(properties);
        for (int i = 0; i <= 3; i++) {
            this.defaultModifiers[i] = getStatsForEnchantmentLevel(i);
        }
    }

    private ImmutableMultimap<Holder<Attribute>, AttributeModifier> getStatsForEnchantmentLevel(int swiftwoodLevel) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0D, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) Math.min(0, -3.75F + 0.15F * swiftwoodLevel), AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtEntity, LivingEntity player) {
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        if (!hurtEntity.level().isClientSide) {
            SoundEvent soundEvent = ACSoundRegistry.PRIMITIVE_CLUB_MISS.get();
            if (hurtEntity.getRandom().nextFloat() < 0.8F) {
                MobEffectInstance instance = new MobEffectInstance(ACEffectRegistry.STUNNED, 150 + hurtEntity.getRandom().nextInt(150), 0, false, false);
                if (hurtEntity.addEffect(instance)) {
                    AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(hurtEntity.getId(), player.getId(), 3, instance.getDuration()));
                    soundEvent = ACSoundRegistry.PRIMITIVE_CLUB_HIT.get();
                    int dazingEdgeLevel = 0;
                    if(player instanceof net.minecraft.world.entity.player.Player realPlayer){
                        dazingEdgeLevel = stack.getEnchantmentLevel(realPlayer.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.DAZING_SWEEP));
                    }
                    if (dazingEdgeLevel > 0) {
                        float f = dazingEdgeLevel + 1.2F;
                        AABB aabb = AABB.ofSize(hurtEntity.position(), f, f, f);
                        for (Entity entity : hurtEntity.level().getEntities(player, aabb, Entity::canBeHitByProjectile)) {
                            if (!entity.is(hurtEntity) && !entity.isAlliedTo(player) && entity.distanceTo(hurtEntity) <= f && entity instanceof LivingEntity inflict) {
                                MobEffectInstance instance2 = new MobEffectInstance(ACEffectRegistry.STUNNED, 80 + hurtEntity.getRandom().nextInt(80), 0, false, false);
                                inflict.hurt(inflict.level().damageSources().mobAttack(player), 1.0F);
                                if (inflict.addEffect(instance2)) {
                                    AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(inflict.getId(), player.getId(), 3, instance2.getDuration()));
                                }
                            }
                        }
                    }
                }
            }
            player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), soundEvent, player.getSoundSource(), 1.0F, 1.0F);

        }
        return true;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos blockPos, LivingEntity
            livingEntity) {
        if ((double) state.getDestroySpeed(level, blockPos) != 0.0D) {
            itemStack.hurtAndBreak(2, livingEntity, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            int swift = 0;
            if(stack.getItem() instanceof PrimitiveClubItem){
                // Get enchantment level from registry access if available
                swift = 0; // Default to 0 since we can't access registry here
            }
            return defaultModifiers[Mth.clamp(swift, 0, 3)];
        }
        return ImmutableMultimap.of();
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack repairItem) {
        return repairItem.is(ACItemRegistry.HEAVY_BONE.get()) || super.isValidRepairItem(item, repairItem);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return player.getAttackStrengthScale(0) < 0.95 || player.attackAnim != 0;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getAttackStrengthScale(0) < 1 && player.attackAnim > 0) {
                return true;
            } else {
                player.swingTime = -1;
            }
        }
        return false;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        if (entity instanceof Player player && held) {
            if (player.getAttackStrengthScale(0) < 0.95 && player.attackAnim > 0) {
                player.swingTime--;
            }
        }
    }
}
