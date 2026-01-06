package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class SodaBottleRocketEntity extends Projectile implements ItemSupplier {

    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(SodaBottleRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    
    private int lifetime;
    private int life;

    public SodaBottleRocketEntity(EntityType<? extends SodaBottleRocketEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SodaBottleRocketEntity(Level worldIn, double x, double y, double z, ItemStack givenItem) {
        super(ACEntityRegistry.SODA_BOTTLE_ROCKET.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.lifetime = 18 + this.random.nextInt(14);
    }

    public SodaBottleRocketEntity(Level level, @Nullable Entity entity, double x, double y, double z, ItemStack stack) {
        this(level, x, y, z, stack);
        this.setOwner(entity);
    }

    public SodaBottleRocketEntity(Level level, ItemStack stack, LivingEntity livingEntity) {
        this(level, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), stack);
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(livingEntity.getId()));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.life = tag.getInt("Life");
        this.lifetime = tag.getInt("LifeTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", this.life);
        tag.putInt("LifeTime", this.lifetime);
    }

    public void tick() {
        super.tick();
        
        if (this.isAttachedToEntity()) {
            Entity attached = this.getAttachedEntity();
            if (attached != null) {
                if (attached.isRemoved()) {
                    this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
                } else {
                    this.setPos(attached.getX(), attached.getY() + attached.getBbHeight() * 0.5, attached.getZ());
                    this.setDeltaMovement(Vec3.ZERO);
                }
            }
        } else {
            if (!this.isShotAtAngle()) {
                double d2 = this.horizontalCollision ? 1.0D : 1.15D;
                this.setDeltaMovement(this.getDeltaMovement().multiply(d2, 1.0D, d2).add(0.0D, 0.04D, 0.0D));
            }
            
            Vec3 vec3 = this.getDeltaMovement();
            this.move(MoverType.SELF, vec3);
            this.setDeltaMovement(vec3);
        }
        
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }
        
        this.updateRotation();
        
        if (this.life == 0 && !this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
        }
        
        ++this.life;
        
        if (this.level().isClientSide) {
            for(int i = 0; i < 5; i++){
                this.level().addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), this.getX(), this.getY() - 0.3D, this.getZ(), this.random.nextGaussian() * 0.25D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.25D);
            }
        }
        
        if (!this.level().isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    private boolean isAttachedToEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent();
    }

    @Nullable
    private Entity getAttachedEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent() 
            ? this.level().getEntity(this.entityData.get(DATA_ATTACHED_TO_TARGET).getAsInt()) 
            : null;
    }

    private boolean isShotAtAngle() {
        return false;
    }

    private void explode() {
        this.level().broadcastEntityEvent(this, (byte) 17);
        this.discard();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 17) {
            this.level().addParticle(ACParticleRegistry.FROSTMINT_EXPLOSION.get(), this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
            for(int i = 0; i < this.random.nextInt(15) + 30; ++i) {
                this.level().addParticle(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D);
            }
            for(int i = 0; i < this.random.nextInt(15) + 15; ++i) {
                this.level().addParticle(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), this.getX() + this.random.nextGaussian() * 0.95D, this.getY() + this.random.nextGaussian() * 0.95D, this.getZ() + this.random.nextGaussian() * 0.95D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D);
            }
            SoundEvent soundEvent = AlexsCaves.PROXY.isFarFromCamera(this.getX(), this.getY(), this.getZ()) ? SoundEvents.FIREWORK_ROCKET_BLAST : SoundEvents.FIREWORK_ROCKET_BLAST_FAR;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE_ROCKET.get());
    }
}
