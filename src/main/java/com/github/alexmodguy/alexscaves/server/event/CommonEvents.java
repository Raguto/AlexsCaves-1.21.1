package com.github.alexmodguy.alexscaves.server.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SeekingArrowEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.ExtinctionSpearItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeMapHolder;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.level.biome.MultiNoiseBiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.SugarRushEffect;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.google.common.collect.ImmutableSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommonEvents {

    @SubscribeEvent
    public void livingDie(LivingDeathEvent event) {
        if (event.getEntity().getType() == EntityType.MAGMA_CUBE && event.getSource() != null && event.getSource().getEntity() instanceof Frog frog) {
            // In 1.21, frog.getVariant() returns Holder<FrogVariant>
            if (frog.getVariant().is(ACFrogRegistry.PRIMORDIAL)) {
                event.getEntity().spawnAtLocation(new ItemStack(ACBlockRegistry.CARMINE_FROGLIGHT.get()));
            }
        }
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Mob mob && event.getSource() != null && event.getSource().getDirectEntity() instanceof LivingEntity directSource && directSource.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.PRIMITIVE_CLUB.get())) {
            // Enchantment checking is now data-driven in 1.21 - skip this feature for now
            // The BONKING enchantment would need to be checked via the new enchantment system
        }
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().getUUID().toString().equals("71363abe-fd03-49c9-940d-aae8b8209b7c")) {
                event.getEntity().spawnAtLocation(new ItemStack(ACItemRegistry.GREEN_SOYLENT.get(), 1 + event.getEntity().getRandom().nextInt(9)));
            }
            if (event.getEntity().getUUID().toString().equals("4a463319-625c-4b86-a4e7-8b700f023a60")) {
                event.getEntity().spawnAtLocation(new ItemStack(ACItemRegistry.STINKY_FISH.get(), 1));
            }
        }
    }

    @SubscribeEvent
    public void livingHeal(LivingHealEvent event) {
        // In 1.21, DeferredHolder IS a Holder - don't call .get()
        if (event.getEntity().hasEffect(ACEffectRegistry.IRRADIATED) && !event.getEntity().getType().is(ACTagRegistry.RESISTS_RADIATION)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void playerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(ACItemRegistry.HOLOCODER.get()) && event.getTarget() instanceof LivingEntity && !(event.getTarget() instanceof ArmorStand) && event.getTarget().isAlive()) {
            // In 1.21, use DataComponents instead of NBT tags
            CompoundTag tag = new CompoundTag();
            tag.putUUID("BoundEntityUUID", event.getTarget().getUUID());
            CompoundTag entityTag = event.getTarget() instanceof Player ? new CompoundTag() : event.getTarget().serializeNBT(event.getLevel().registryAccess());
            entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(event.getTarget().getType()).toString());
            if (event.getTarget() instanceof Player) {
                entityTag.putUUID("UUID", event.getTarget().getUUID());
            }
            tag.put("BoundEntityTag", entityTag);
            ItemStack stackReplacement = new ItemStack(ACItemRegistry.HOLOCODER.get());
            stack.shrink(1);
            stackReplacement.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            event.getEntity().swing(event.getHand());
            if (!event.getEntity().addItem(stackReplacement)) {
                ItemEntity itementity = event.getEntity().drop(stackReplacement, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setThrower(event.getEntity());
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public void livingFindTarget(LivingChangeTargetEvent event) {
        // In 1.21, use getNewAboutToBeSetTarget() instead of getNewTarget()
        if (event.getEntity() instanceof Mob mob && event.getNewAboutToBeSetTarget() instanceof VallumraptorEntity vallumraptor && vallumraptor.getHideFor() > 0) {
            mob.setTarget(null);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingHurt(LivingDamageEvent.Pre event) {
        // In 1.21, LivingDamageEvent is split into Pre and Post
        if (event.getEntity().isPassenger() && event.getEntity() instanceof FlyingMount && (event.getSource().is(DamageTypes.IN_WALL) || event.getSource().is(DamageTypes.FALL) || event.getSource().is(DamageTypes.FLY_INTO_WALL))) {
            event.setNewDamage(0);
        }
        if (event.getEntity() instanceof WatcherPossessionAccessor possessed && possessed.isPossessedByWatcher() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !(event.getSource().getEntity() instanceof WatcherEntity)) {
            event.setNewDamage(0);
        }
        if (event.getEntity() instanceof Player player && player.getUseItem().is(ACItemRegistry.EXTINCTION_SPEAR.get()) && ExtinctionSpearItem.killGrottoGhostsFor(player, true)) {
            event.setNewDamage(0);
            player.playSound(SoundEvents.SHIELD_BLOCK);
        }
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.FALL) && player.getItemBySlot(EquipmentSlot.FEET).is(ACItemRegistry.RAINBOUNCE_BOOTS.get())) {
            player.fallDistance = 0.0F;
            event.setNewDamage(0);
        }
    }


    @SubscribeEvent
    public void livingAttack(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow && event.getEntity().isBlocking() && event.getEntity().getUseItem().is(ACItemRegistry.RESISTOR_SHIELD.get())) {
            // Enchantment checking is now data-driven in 1.21 - skip for now
            // Would need to check via new enchantment system
        }
        // In 1.21, DeferredHolder IS a Holder - don't call .get()
        if (event.getSource() != null && event.getSource().getDirectEntity() instanceof LivingEntity directSource && directSource.hasEffect(ACEffectRegistry.STUNNED)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void playerAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof DinosaurEntity && event.getEntity().isPassengerOfSameVehicle(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        // In 1.21, DeferredHolder IS a Holder - don't call .get()
        if (living.hasEffect(ACEffectRegistry.BUBBLED) && living.isInFluidType()) {
            living.removeEffect(ACEffectRegistry.BUBBLED);
        }
        if (living.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE) && living.tickCount % 5 == 0 && DarknessIncarnateEffect.isInLight(living, 11)) {
            living.removeEffect(ACEffectRegistry.DARKNESS_INCARNATE);
        }
        if (living.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.DIVING_HELMET.get()) && (!living.isEyeInFluid(FluidTags.WATER) || living.getVehicle() instanceof SubmarineEntity)) {
            living.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 810, 0, false, false, true));
        }
        if (!living.level().isClientSide && living instanceof Mob mob && mob.getTarget() instanceof VallumraptorEntity vallumraptor && vallumraptor.getHideFor() > 0) {
            mob.setTarget(null);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(FinalizeSpawnEvent event) {
        try {
            if (event.getEntity() instanceof Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, RaycatEntity.class, 10.0F, 1.0D, 1.2D));
            }
            if (event.getEntity() instanceof Drowned drowned && drowned.level().getBiome(drowned.blockPosition()).is(ACBiomeRegistry.ABYSSAL_CHASM)) {
                if (drowned.getItemBySlot(EquipmentSlot.FEET).isEmpty() && drowned.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && drowned.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && drowned.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance.get()) {
                        drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ACItemRegistry.DIVING_HELMET.get()));
                        drowned.setDropChance(EquipmentSlot.HEAD, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance.get()) {
                        drowned.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ACItemRegistry.DIVING_CHESTPLATE.get()));
                        drowned.setDropChance(EquipmentSlot.CHEST, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance.get()) {
                        drowned.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ACItemRegistry.DIVING_LEGGINGS.get()));
                        drowned.setDropChance(EquipmentSlot.LEGS, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance.get()) {
                        drowned.setItemSlot(EquipmentSlot.FEET, new ItemStack(ACItemRegistry.DIVING_BOOTS.get()));
                        drowned.setDropChance(EquipmentSlot.FEET, 0.5F);
                    }
                }
            }
            if (event.getEntity() instanceof Fox fox) {
                fox.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(fox, GingerbreadManEntity.class, 40, false, false, null));
            }
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void livingRemoveEffect(MobEffectEvent.Remove event) {
        if (event.getEffect() != null && event.getEffect().value() instanceof DarknessIncarnateEffect darknessIncarnateEffect) {
            darknessIncarnateEffect.toggleFlight(event.getEntity(), false);
            event.getEntity().level().playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), ACSoundRegistry.DARKNESS_INCARNATE_EXIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (event.getEffect() != null && event.getEffect().value() instanceof SugarRushEffect) {
            event.getEntity().level().playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), ACSoundRegistry.SUGAR_RUSH_EXIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }


    @SubscribeEvent
    public void livingAddEffect(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect().value() instanceof DarknessIncarnateEffect) {
            event.getEntity().level().playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), ACSoundRegistry.DARKNESS_INCARNATE_ENTER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (event.getEffectInstance().getEffect().value() instanceof SugarRushEffect) {
            event.getEntity().level().playSound(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), ACSoundRegistry.SUGAR_RUSH_ENTER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (event.getEntity() instanceof Player player && event.getEffectInstance().getEffect().value() instanceof SugarRushEffect && AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get()) {
            float timeBetweenTicksIncrease = 2F;
            SugarRushEffect.enterSlowMotion(player, player.level(), Mth.ceil(event.getEffectInstance().getDuration() * timeBetweenTicksIncrease), timeBetweenTicksIncrease);
        }
    }

    @SubscribeEvent
    public void livingExpireEffect(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof DarknessIncarnateEffect darknessIncarnateEffect) {
            darknessIncarnateEffect.toggleFlight(event.getEntity(), false);
            event.getEntity().playSound(ACSoundRegistry.DARKNESS_INCARNATE_EXIT.get());
        }
        if (event.getEntity() instanceof Player player && event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof SugarRushEffect && AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get()) {
            SugarRushEffect.leaveSlowMotion(player, player.level());
        }
    }

    @SubscribeEvent
    public void travelToDimension(EntityTravelToDimensionEvent event) {
        // In 1.21, DeferredHolder IS a Holder - don't call .get()
        if (event.getEntity() instanceof Player player && player.hasEffect(ACEffectRegistry.SUGAR_RUSH)) {
            SugarRushEffect.leaveSlowMotion(player, player.level());
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get()) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(event.getServer());
            tracker.tickRateModifierList.clear();
        }
        // Reset static holders when server stops
        ACBiomeMapHolder.reset();
        com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder.reset();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        AlexsCaves.LOGGER.info("[AC] onServerAboutToStart event fired!");
        ACBiomeRarity.init();
        RegistryAccess registryAccess = event.getServer().registryAccess();
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registries.BIOME);
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        
        // Get the world seed early - this is critical for biome generation
        long worldSeed = event.getServer().getWorldData().worldGenOptions().seed();
        AlexsCaves.LOGGER.info("[AC] Setting world seed {} on biome sources during server start", worldSeed);
        
        // Initialize the static biome map holder FIRST - this is used by the mixin
        ACBiomeMapHolder.initializeFromRegistry(allBiomes);
        
        // Also set the seed in the static holder
        com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder.setSeed(worldSeed);
        com.github.alexmodguy.alexscaves.server.level.biome.ACWorldSeedHolder.setDimension(Level.OVERWORLD);
        
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new HashMap<>();
        for (ResourceKey<Biome> biomeResourceKey : allBiomes.registryKeySet()) {
            Optional<Holder.Reference<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        
        AlexsCaves.LOGGER.info("[AC] LevelStems count: {}", levelStems.registryKeySet().size());
        for (ResourceKey<LevelStem> levelStemResourceKey : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> holderOptional = levelStems.getHolder(levelStemResourceKey);
            if (holderOptional.isPresent() && holderOptional.get().value().generator().getBiomeSource() instanceof BiomeSourceAccessor expandedBiomeSource) {
                expandedBiomeSource.setResourceKeyMap(biomeMap);
                
                AlexsCaves.LOGGER.info("[AC] Found BiomeSourceAccessor for dimension: {}", levelStemResourceKey.location());
                
                // Set the seed and dimension on MultiNoiseBiomeSourceAccessor
                if (expandedBiomeSource instanceof MultiNoiseBiomeSourceAccessor multiNoiseAccessor) {
                    multiNoiseAccessor.setLastSampledSeed(worldSeed);
                    // Set dimension based on level stem
                    if (levelStemResourceKey.equals(LevelStem.OVERWORLD)) {
                        multiNoiseAccessor.setLastSampledDimension(Level.OVERWORLD);
                    } else if (levelStemResourceKey.equals(LevelStem.NETHER)) {
                        multiNoiseAccessor.setLastSampledDimension(Level.NETHER);
                    } else if (levelStemResourceKey.equals(LevelStem.END)) {
                        multiNoiseAccessor.setLastSampledDimension(Level.END);
                    }
                    AlexsCaves.LOGGER.info("[AC] Set seed on biome source for dimension: {}", levelStemResourceKey.location());
                }
                
                if (levelStemResourceKey.equals(LevelStem.OVERWORLD)) {
                    ImmutableSet.Builder<Holder<Biome>> biomeHolders = ImmutableSet.builder();
                    for (ResourceKey<Biome> biomeResourceKey : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
                        Optional<Holder.Reference<Biome>> biomeHolder = allBiomes.getHolder(biomeResourceKey);
                        if (biomeHolder.isPresent()) {
                            biomeHolders.add(biomeHolder.get());
                            AlexsCaves.LOGGER.info("[AC] Adding biome to expansion: {}", biomeResourceKey.location());
                        } else {
                            AlexsCaves.LOGGER.warn("[AC] Biome not found in registry: {}", biomeResourceKey.location());
                        }
                    }
                    ImmutableSet<Holder<Biome>> acBiomes = biomeHolders.build();
                    AlexsCaves.LOGGER.info("[AC] About to expand biome source with {} AC biomes", acBiomes.size());
                    expandedBiomeSource.expandBiomesWith(acBiomes);
                    AlexsCaves.LOGGER.info("[AC] Expanded biome source with {} Alex's Caves biomes", acBiomes.size());
                }
            } else {
                AlexsCaves.LOGGER.warn("[AC] BiomeSource for {} is not a BiomeSourceAccessor", levelStemResourceKey.location());
            }
        }
    }

    @SubscribeEvent
    public void playerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().isCreative()) {
            if (event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(event.getEntity(), EquipmentSlot.MAINHAND);
            }
            if (event.getEntity().getItemInHand(InteractionHand.OFF_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(event.getEntity(), EquipmentSlot.OFFHAND);
            }
        }
    }

    @SubscribeEvent
    public void onVillagerTradeSetup(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CARTOGRAPHER && AlexsCaves.COMMON_CONFIG.cartographersSellCabinMaps.get()) {
            int level = 2;
            List<VillagerTrades.ItemListing> list = event.getTrades().get(level);
            list.add(new VillagerUndergroundCabinMapTrade(5, 10, 6));
            event.getTrades().put(level, list);
        }
    }

    @SubscribeEvent
    public void onWanderingTradeSetup(WandererTradesEvent event) {
        if (AlexsCaves.COMMON_CONFIG.wanderingTradersSellCabinMaps.get()) {
            event.getGenericTrades().add(new VillagerUndergroundCabinMapTrade(8, 1, 10));
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AlexsCaves.COMMON_CONFIG.warnGenerationIncompatibility.get() && !AlexsCaves.MOD_GENERATION_CONFLICTS.isEmpty() && event.getEntity().level().isClientSide) {
            for (String modid : AlexsCaves.MOD_GENERATION_CONFLICTS) {
                if (ModList.get().isLoaded(modid)) {
                    event.getEntity().sendSystemMessage(Component.translatable("alexscaves.startup_warning.generation_incompatible", modid).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    private static void checkAndDestroyExploitItem(Player player, EquipmentSlot slot) {
        ItemStack itemInHand = player.getItemBySlot(slot);
        if (itemInHand.is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
            // In 1.21, use DataComponents instead of getTag()
            CustomData customData = itemInHand.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (itemTagContainsAC(tag, "BiomeKey", false) || itemTagContainsAC(tag, "Structure", true) || itemTagContainsAC(tag, "structurecompass:structureName", true) || itemTagContainsAC(tag, "StructureKey", true)) {
                    itemInHand.shrink(1);
                    // In 1.21, broadcastBreakEvent was removed - use onEquippedItemBroken or similar
                    player.playSound(ACSoundRegistry.DISAPPOINTMENT.get());
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("item.alexscaves.natures_compass_warning"), true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (event.getItemStack().getItem() == Items.GLASS_BOTTLE) {
            HitResult raytraceresult = getPlayerPOVHitResult(event.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (event.getLevel().mayInteract(player, blockpos)) {
                    if (event.getLevel().getFluidState(blockpos).getFluidType() == ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get()) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        event.getLevel().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        if (!player.addItem(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE.get()))) {
                            player.spawnAtLocation(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE.get()));
                        }
                        player.swing(event.getHand());
                        if (!player.isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }
    }

    private static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        // In 1.21, use blockInteractionRange() instead of getBlockReach()
        double d0 = player.blockInteractionRange();
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluid, player));
    }

    // Anvil update event removed - enchantment system is data-driven in 1.21

    private static boolean itemTagContainsAC(CompoundTag tag, String tagID, boolean allowUndergroundCabin) {
        if (tag.contains(tagID)) {
            String resourceLocation = tag.getString(tagID);
            return resourceLocation.contains("alexscaves:") && (!allowUndergroundCabin || !resourceLocation.contains("underground_cabin"));
        }
        return false;
    }
}
