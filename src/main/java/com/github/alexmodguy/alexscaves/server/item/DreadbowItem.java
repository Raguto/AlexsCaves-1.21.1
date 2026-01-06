package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DarkArrowEntity;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DreadbowItem extends ProjectileWeaponItem implements UpdatesStackTags {

    public DreadbowItem() {
        super(new Item.Properties().rarity(ACItemRegistry.RARITY_DEMONIC).durability(500));
    }

    @Nullable
    public static EntityType getTypeOfArrow(ItemStack itemStackIn) {
        CompoundTag tag = itemStackIn.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(tag != null && tag.contains("LastUsedArrowType")) {
            String str = tag.getString("LastUsedArrowType");
            return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(str));
        }
        return null;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        ItemStack ammo = player.getProjectile(itemstack);
        boolean flag = player.isCreative();
        if(flag || !ammo.isEmpty()){
            AbstractArrow lastArrow = createArrow(player, itemstack, ItemStack.EMPTY);
            EntityType lastArrowType = lastArrow == null ? EntityType.ARROW : lastArrow.getType();
            CompoundTag tag = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putString("LastUsedArrowType", BuiltInRegistries.ENTITY_TYPE.getKey(lastArrowType).toString());
            itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemstack);
        }else{
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean using = entity instanceof LivingEntity living && living.getUseItem().equals(stack);
        int useTime = getUseTime(stack);
        if (level.isClientSide) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.getInt("PrevUseTime") != tag.getInt("UseTime")) {
                tag.putInt("PrevUseTime", getUseTime(stack));
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }

            if (using && getPerfectShotTicks(stack) > 0) {
                setPerfectShotTicks(stack, getPerfectShotTicks(stack) - 1);
                AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(entity.getId(), stack));
            }
            boolean relentless = stack.getEnchantmentLevel(entity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.RELENTLESS_DARKNESS)) > 0;
            int twilightPerfection = stack.getEnchantmentLevel(entity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.TWILIGHT_PERFECTION));
            int maxLoadTime = getMaxLoadTime(stack, entity);
            if (using && useTime < maxLoadTime) {
                int set = useTime + (relentless ? 3 : 1);
                setUseTime(stack, set);
                if(twilightPerfection > 0){
                    if(set >= maxLoadTime && useTime <= maxLoadTime){
                        setPerfectShotTicks(stack, 4 + (twilightPerfection - 1) * 3);
                        AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(entity.getId(), stack));
                    }else{
                        setPerfectShotTicks(stack, 0);
                        AlexsCaves.sendMSGToServer(new UpdateItemTagMessage(entity.getId(), stack));
                    }
                }
            }
            if(relentless){
                if (using && useTime >= maxLoadTime) {
                    setUseTime(stack, 0);
                }
            }
            if (!using && useTime > 0.0F) {
                setUseTime(stack, Math.max(0, useTime - 5));
                setPerfectShotTicks(stack, 0);
            }
            if(using){
                Vec3 particlePos = entity.position().add((level.random.nextFloat() - 0.5F) * 2.5F, 0F, (level.random.nextFloat() - 0.5F) * 2.5F);
                level.addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), particlePos.x, particlePos.y, particlePos.z, entity.getX(), entity.getY(0.5F), entity.getZ());
            }
        }
    }

    private static int getMaxLoadTime(ItemStack stack, Entity entity) {
        if(stack.getEnchantmentLevel(entity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.RELENTLESS_DARKNESS)) > 0){
            return 5;
        }else{
            return 40 - 8 * stack.getEnchantmentLevel(entity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.DARK_NOCK));
        }
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return compoundtag != null ? compoundtag.getInt("UseTime") : 0;
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("PrevUseTime", getUseTime(stack));
        tag.putInt("UseTime", useTime);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    public static int getPerfectShotTicks(ItemStack stack) {
        CompoundTag compoundtag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return compoundtag != null ? compoundtag.getInt("PerfectShotTicks") : 0;
    }

    public static void setPerfectShotTicks(ItemStack stack, int ticks) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("PerfectShotTicks", ticks);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        float prev = compoundtag != null ? (float) compoundtag.getInt("PrevUseTime") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    public static float getPullingAmount(ItemStack itemStack, float partialTicks, Entity entity){
        return Math.min(getLerpedUseTime(itemStack, partialTicks) / (float) getMaxLoadTime(itemStack, entity), 1F);
    }


    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public static float getPowerForTime(int i, ItemStack itemStack, Entity entity) {
        float f = (float) i / (float)getMaxLoadTime(itemStack, entity);
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i1) {
        if (livingEntity instanceof Player player && itemStack.getEnchantmentLevel(livingEntity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.RELENTLESS_DARKNESS)) <= 0) {
            int i = this.getUseDuration(itemStack, livingEntity) - i1;
            float f = getPowerForTime(i, itemStack, livingEntity);
            boolean precise = itemStack.getEnchantmentLevel(livingEntity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.PRECISE_VOLLEY)) > 0;
            boolean respite = itemStack.getEnchantmentLevel(livingEntity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.SHADED_RESPITE)) > 0 && !DarknessIncarnateEffect.isInLight(player, 11);
            boolean perfectShot = itemStack.getEnchantmentLevel(livingEntity.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.TWILIGHT_PERFECTION)) > 0 && getPerfectShotTicks(itemStack) > 0;
            if (f > 0.1D) {
                player.playSound(ACSoundRegistry.DREADBOW_RELEASE.get());
                ItemStack ammoStack = player.getProjectile(itemStack);
                if(respite && ammoStack.isEmpty()){
                    ammoStack = new ItemStack(Items.ARROW);
                }
                AbstractArrow abstractArrow = createArrow(player, itemStack, ammoStack);
                if(abstractArrow != null){
                    float maxDist = 128 * f;
                    HitResult realHitResult = ProjectileUtil.getHitResultOnViewVector(player, Entity::canBeHitByProjectile, maxDist);
                    if(realHitResult.getType() == HitResult.Type.MISS){
                        realHitResult = ProjectileUtil.getHitResultOnViewVector(player, Entity::canBeHitByProjectile, f * 42);
                    }
                    BlockPos mutableSkyPos = new BlockPos.MutableBlockPos(realHitResult.getLocation().x, realHitResult.getLocation().y + 1.5, realHitResult.getLocation().z);
                    int maxFallHeight = 15;
                    int k = 0;
                    while(mutableSkyPos.getY() < level.getMaxBuildHeight() && level.isEmptyBlock(mutableSkyPos) && k < maxFallHeight){
                        mutableSkyPos = mutableSkyPos.above();
                        k++;
                    }
                    boolean darkArrows = isConvertibleArrow(abstractArrow);
                    int maxArrows = darkArrows ? 30 : 8;
                    abstractArrow.pickup = AbstractArrow.Pickup.ALLOWED;
                    for(int j = 0; j < Math.ceil(maxArrows * f); j++){
                        if(darkArrows){
                            DarkArrowEntity darkArrowEntity = new DarkArrowEntity(level, livingEntity);
                            darkArrowEntity.setShadowArrowDamage(precise ? 2.0F : 3.0F);
                            darkArrowEntity.setPerfectShot(perfectShot);
                            abstractArrow = darkArrowEntity;
                        }else if(perfectShot){
                            abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() * 2.0F);
                        }
                        Vec3 vec3 = mutableSkyPos.getCenter().add(level.random.nextFloat() * 16 - 8, level.random.nextFloat() * 4 - 2, level.random.nextFloat() * 16 - 8);
                        int clearTries = 0;
                        while (clearTries < 6 && !level.isEmptyBlock(BlockPos.containing(vec3)) && level.getFluidState(BlockPos.containing(vec3)).isEmpty()){
                            clearTries++;
                            vec3 = mutableSkyPos.getCenter().add(level.random.nextFloat() * 16 - 8, level.random.nextFloat() * 4 - 2, level.random.nextFloat() * 16 - 8);
                        }
                        if(!level.isEmptyBlock(BlockPos.containing(vec3)) && level.getFluidState(BlockPos.containing(vec3)).isEmpty()){
                            vec3 = mutableSkyPos.getCenter();
                        }
                        abstractArrow.setPos(vec3);
                        Vec3 vec31 = realHitResult.getLocation().subtract(vec3);
                        float randomness = precise ? 0.0F : (darkArrows ? 20F : 5F) + level.random.nextFloat() * 10F;
                        if(!precise && level.random.nextFloat() < 0.25F){
                            randomness = level.random.nextFloat();
                        }
                        abstractArrow.shoot(vec31.x, vec31.y, vec31.z, 0.5F + 1.5F * level.random.nextFloat(),  randomness);
                        level.addFreshEntity(abstractArrow);
                        abstractArrow = createArrow(player, itemStack, ammoStack);
                        abstractArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    if(darkArrows){
                        Vec3 vec3 = realHitResult.getLocation();
                        level.playSound((Player)null, vec3.x, vec3.y, vec3.z, ACSoundRegistry.DREADBOW_RAIN.get(), SoundSource.PLAYERS, 12.0F, 1.0F);
                    }
                    if(!player.isCreative()){
                        if(!respite){
                            itemStack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                        }
                        if(!respite || !ammoStack.is(Items.ARROW)){
                            ammoStack.shrink(1);
                        }
                    }
                }
            }
        }
    }


    public void onUseTick(Level level, LivingEntity living, ItemStack itemStack, int timeUsing) {
        super.onUseTick(level, living, itemStack, timeUsing);
        if(living instanceof Player player && itemStack.getEnchantmentLevel(living.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.RELENTLESS_DARKNESS)) > 0 && timeUsing % 3 == 0){
            boolean respite = itemStack.getEnchantmentLevel(living.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(ACEnchantmentRegistry.SHADED_RESPITE)) > 0 && !DarknessIncarnateEffect.isInLight(living, 11);
            player.playSound(ACSoundRegistry.DREADBOW_RELEASE.get());
            ItemStack ammoStack = player.getProjectile(itemStack);
            if(respite && ammoStack.isEmpty()){
                ammoStack = new ItemStack(Items.ARROW);
            }
            AbstractArrow abstractArrow = createArrow(player, itemStack, ammoStack);
            boolean darkArrows = isConvertibleArrow(abstractArrow);
            int maxArrows = darkArrows ? 1 + living.getRandom().nextInt(2) : 1;
            float randomness = 0.5F;
            for(int i = 0; i < maxArrows; i++){
                abstractArrow.pickup = AbstractArrow.Pickup.ALLOWED;
                if(darkArrows){
                    DarkArrowEntity darkArrowEntity = new DarkArrowEntity(level, living);
                    darkArrowEntity.setShadowArrowDamage(2.0F);
                    abstractArrow = darkArrowEntity;
                }
                abstractArrow.setPos(abstractArrow.position().add(level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F));
                Vec3 vec3 = player.getViewVector(1.0F);
                abstractArrow.shoot(vec3.x, vec3.y, vec3.z, 4F + 3F * level.random.nextFloat(),  randomness);
                randomness += 2.0F;
                level.addFreshEntity(abstractArrow);
                abstractArrow = createArrow(player, itemStack, ammoStack);
                abstractArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            if(!player.isCreative()){
                if(!respite){
                    itemStack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                }
                if(!respite || !ammoStack.is(Items.ARROW)){
                    ammoStack.shrink(1);
                }
            }
        }
    }

    // Required override for 1.21 ProjectileWeaponItem
    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
    }

    private AbstractArrow createArrow(Player player, ItemStack bowStack, ItemStack ammoIn) {
        ItemStack ammo = ammoIn.isEmpty() ? player.getProjectile(bowStack) : ammoIn;
        ArrowItem arrowitem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
        AbstractArrow abstractArrow =  arrowitem.createArrow(player.level(), ammo, player, bowStack);
        return abstractArrow;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(ACItemRegistry.DREADBOW.get()) || !newStack.is(ACItemRegistry.DREADBOW.get());
    }
    public static boolean isConvertibleArrow(Entity arrowEntity){
        return arrowEntity instanceof Arrow arrow && arrow.getColor() == -1;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 64;
    }
}
