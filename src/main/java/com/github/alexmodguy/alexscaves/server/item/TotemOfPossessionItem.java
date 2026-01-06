package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.TotemExplosion;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class TotemOfPossessionItem extends Item implements UpdatesStackTags {
    private static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_damage");
    private static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath("alexscaves", "base_attack_speed");
    private final Multimap<Holder<Attribute>, AttributeModifier> defaultModifiers;

    public TotemOfPossessionItem() {
        super(new Item.Properties().durability(1000).rarity(Rarity.UNCOMMON));
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2.0D, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) -2.4F, AttributeModifier.Operation.ADD_VALUE));
        this.defaultModifiers = builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            updateEntityIdFromServer(serverLevel, player, itemstack);
        }
        Entity controlledEntity = getControlledEntity(level, itemstack);
        if (isBound(itemstack) && (controlledEntity == null || !controlledEntity.isAlive()) && !level.isClientSide) {
            setPossessed(controlledEntity, false);
            resetBound(itemstack);
        }
        if (isBound(itemstack) && controlledEntity != null) {
            int sightlessLevel = 0;
            if(player instanceof Player){
                sightlessLevel = itemstack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.SIGHTLESS));
            }
            if (isEntityLookingAt(player, controlledEntity, 5F) || sightlessLevel > 0) {
                player.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE.get());
                player.startUsingItem(interactionHand);
                return InteractionResultHolder.consume(itemstack);
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int i1) {
        Entity controlledEntity = getControlledEntity(level, stack);
        if (controlledEntity != null) {
            controlledEntity.setGlowingTag(false);
        }

        if (level.isClientSide) {
            AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(user.getId(), stack));
        }
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
        }
    }

    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int timeUsing) {
        Entity controlledEntity = getControlledEntity(level, stack);

        if (isBound(stack) && (controlledEntity == null || !controlledEntity.isAlive()) || stack.getDamageValue() >= stack.getMaxDamage()) {
            int detonatingDeathLevel = 0;
            if(user instanceof Player player){
                detonatingDeathLevel = stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.DETONATING_DEATH));
            }
            if (controlledEntity != null && detonatingDeathLevel > 0) {
                TotemExplosion explosion = new TotemExplosion(level, user, controlledEntity.getX(), controlledEntity.getY(), controlledEntity.getZ(), 2F + (float) Math.floor(controlledEntity.getBbWidth()), Explosion.BlockInteraction.KEEP);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
            setPossessed(controlledEntity, false);
            resetBound(stack);
            user.stopUsingItem();
            if (level.isClientSide) {
                AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(user.getId(), stack));
            }
            return;
        }
        if (!isBound(stack) || controlledEntity == null || !isEntityLookingAt(user, controlledEntity, 5F) && (user instanceof Player player ? stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.SIGHTLESS)) == 0 : true) || controlledEntity instanceof Player && !AlexsCaves.COMMON_CONFIG.totemOfPossessionPlayers.get()) {

            user.stopUsingItem();
            if (level.isClientSide) {
                AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(user.getId(), stack));
            }
            return;
        }

        if (timeUsing % 2 == 0 && level.isClientSide && !(user instanceof Player player && player.isCreative())) {
            stack.setDamageValue(stack.getDamageValue() + 1);
        }

        int i = getUseDuration(stack, user) - timeUsing;
        int realStart = 15;
        float time = i < realStart ? i / (float) realStart : 1F;
        float maxDist = 32.0F * time;
        float speed = 1.25F;
        int sightlessLevel = 0;
        if(user instanceof Player player){
            speed += 0.35F * stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.RAPID_POSSESSION));
            sightlessLevel = stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.SIGHTLESS));
        }
        HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(user, entity -> entity.canBeHitByProjectile() && !entity.equals(controlledEntity), maxDist);
        Vec3 vec3 = hitResult.getLocation();
        if (controlledEntity instanceof Mob mob) {
            PathNavigation pathNavigation = mob.getNavigation();
            pathNavigation.moveTo(vec3.x, vec3.y, vec3.z, time * speed);
            if (sightlessLevel > 0) {
                controlledEntity.setGlowingTag(true);
            }
        } else {
            boolean flying = controlledEntity instanceof FlyingAnimal || controlledEntity instanceof FlyingMob;
            Vec3 vec31 = vec3.subtract(controlledEntity.position());
            boolean jumpFlag = false;
            if (!flying && controlledEntity.horizontalCollision && controlledEntity.onGround() && vec31.y > 0) {
                jumpFlag = true;
            } else if (!flying && vec31.y > 0) {
                vec31 = new Vec3(vec31.x, 0, vec31.z);
            }
            float yaw = -((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float) Math.PI);
            if (vec31.length() > 1F) {
                vec31 = vec31.normalize();
                if (!level.isClientSide) {
                    controlledEntity.setYRot(yaw);
                    controlledEntity.setYBodyRot(controlledEntity.getYRot());
                }
            }
            Vec3 jumpAdd = vec31.scale(0.15F * speed);
            if (jumpFlag) {
                jumpAdd = jumpAdd.add(0, 0.6, 0);
            }
            controlledEntity.setDeltaMovement(controlledEntity.getDeltaMovement().scale(0.8F).add(jumpAdd));
        }
        if (level.isClientSide) {
            for (int particles = 0; particles < 1 + controlledEntity.getBbWidth() * 2; particles++) {
                level.addParticle(DustParticleOptions.REDSTONE, (double) controlledEntity.getRandomX(0.75F), (double) controlledEntity.getRandomY(), (double) controlledEntity.getRandomZ(0.75F), 0.0D, 0.0D, 0.0D);
            }
        } else {
            AABB hitBox = controlledEntity.getBoundingBox().inflate(3F);
            if (controlledEntity instanceof Player || controlledEntity instanceof Mob) {
                for (Entity entity : level.getEntities(controlledEntity, hitBox, Entity::canBeHitByProjectile)) {
                    if (!controlledEntity.is(entity) && !controlledEntity.isAlliedTo(entity) && !entity.is(user) && !entity.isAlliedTo(controlledEntity) && !entity.isPassengerOfSameVehicle(controlledEntity)) {
                        if (entity instanceof LivingEntity target) {
                            if (controlledEntity instanceof Mob mob) {
                                mob.setTarget(target);
                                mob.setLastHurtByMob(target);
                                int astralTransferringLevel = 0;
                                if(user instanceof Player player){
                                    astralTransferringLevel = stack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.ASTRAL_TRANSFERRING));
                                }
                                if (i % 4 == 0 && target.getHealth() > mob.getHealth() && !target.getType().is(ACTagRegistry.RESISTS_TOTEM_OF_POSSESSION) && astralTransferringLevel > 0) {
                                    setPossessed(target, true);
                                    CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
                                    tag.putUUID("BoundEntityUUID", target.getUUID());
                                    CompoundTag entityTag = target.serializeNBT(level.registryAccess());
                                    entityTag.putString("id", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
                                    tag.put("BoundEntityTag", entityTag);
                                    stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
                                    user.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE.get());
                                    if (level instanceof ServerLevel serverLevel && user instanceof Player player) {
                                        updateEntityIdFromServer(serverLevel, player, stack);
                                    }
                                }
                            } else if (controlledEntity instanceof Player player) {
                                player.attack(target);
                                player.resetAttackStrengthTicker();
                            }
                        }
                    }
                }
            }
        }
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }


    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : ImmutableMultimap.of();
    }

    private static void resetBound(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        tag.remove("BoundEntityTag");
        tag.remove("BoundEntityUUID");
        tag.remove("ControllingEntityID");
        itemStack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }


    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (!tag.isEmpty()) {
            Tag entity = tag.get("BoundEntityTag");
            if (entity instanceof CompoundTag) {
                Optional<EntityType<?>> optional = EntityType.by((CompoundTag) entity);
                if (optional.isPresent()) {
                    Component untranslated = optional.get().getDescription().copy().withStyle(ChatFormatting.GRAY);
                    tooltip.add(untranslated);
                }
            }
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    public static UUID getBoundEntityUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (!tag.isEmpty()) {
            return tag.contains("BoundEntityUUID") ? tag.getUUID("BoundEntityUUID") : null;
        } else {
            return null;
        }
    }

    private static void updateEntityIdFromServer(ServerLevel level, Player player, ItemStack itemStack) {
        UUID uuid = getBoundEntityUUID(itemStack);
        CompoundTag tag = itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        int prev = !tag.contains("ControllingEntityID") ? -1 : tag.getInt("ControllingEntityID");
        int set = -1;
        if (uuid != null) {
            Entity entity = level.getEntity(uuid);
            set = entity == null ? -1 : entity.getId();
        }
        tag.putInt("ControllingEntityID", set);
        itemStack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        if (prev != set) {
            AlexsCaves.sendMSGToAll(new UpdateItemTagMessage(player.getId(), itemStack));
        }
    }

    private Entity getControlledEntity(Level level, ItemStack itemStack) {
        if (level.isClientSide) {
            CompoundTag tag = itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            int id = tag.contains("ControllingEntityID") ? tag.getInt("ControllingEntityID") : -1;
            return id == -1 ? null : level.getEntity(id);
        } else if (level instanceof ServerLevel serverLevel) {
            UUID uuid = getBoundEntityUUID(itemStack);
            return uuid == null ? null : serverLevel.getEntity(uuid);
        } else {
            return null;
        }
    }

    private static boolean isEntityLookingAt(LivingEntity looker, Entity seen, double degree) {
        degree *= 1 + (looker.distanceTo(seen) * 0.1);
        Vec3 vec3 = looker.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(seen.getX() - looker.getX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getY() + (double) looker.getEyeHeight()), seen.getZ() - looker.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - degree / d0 && looker.hasLineOfSight(seen);
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundEntityUUID(stack) != null;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtMob, LivingEntity livingEntity1) {
        if (hurtMob.getType().is(ACTagRegistry.RESISTS_TOTEM_OF_POSSESSION) || hurtMob instanceof Player && !AlexsCaves.COMMON_CONFIG.totemOfPossessionPlayers.get()) {
            if (livingEntity1 instanceof Player player) {
                player.displayClientMessage(Component.translatable("item.alexscaves.totem_of_possession.invalid"), true);
            }
        } else {
            setPossessed(hurtMob, true);
            CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
            tag.putUUID("BoundEntityUUID", hurtMob.getUUID());
            CompoundTag entityTag = hurtMob.serializeNBT(hurtMob.level().registryAccess());
            entityTag.putString("id", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(hurtMob.getType()).toString());
            tag.put("BoundEntityTag", entityTag);
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            livingEntity1.playSound(ACSoundRegistry.TOTEM_OF_POSSESSION_USE.get());
        }

        return true;
    }

    private static void setPossessed(@Nullable Entity e, boolean v) {
        if (e == null) return;
        if (v) {
            e.getPersistentData().putBoolean("TotemPossessed", true);
        } else {
            e.getPersistentData().remove("TotemPossessed");
        }
    }

}


