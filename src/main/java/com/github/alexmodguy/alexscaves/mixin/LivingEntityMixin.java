package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HeadRotationEntityAccessor, WatcherPossessionAccessor, DarknessIncarnateUserAccessor, EntityDropChanceAccessor, FrostmintFreezableAccessor {

    @Shadow
    public abstract float getYHeadRot();

    @Shadow
    public float yHeadRotO;

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    @Final
    public WalkAnimationState walkAnimation;
    @Shadow public float yHeadRot;

    @Shadow public abstract boolean addEffect(MobEffectInstance p_21165_);

    @Shadow public abstract boolean canFreeze();

    private float prevHeadYaw;
    private float prevHeadYaw0;
    private float prevHeadPitch;
    private float prevHeadPitch0;

    private boolean watcherPossessionFlag;
    private boolean slowFallingFlag;
    private boolean frostmintFreezingFlag;
    private boolean hadDarknessIncarnateEffect;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;calculateEntityAnimation(Z)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_calculateEntityAnimation(boolean b, CallbackInfo ci) {
        if (MagnetUtil.isPulledByMagnets(this) && ((MagneticEntityAccessor) this).getMagneticAttachmentFace() != Direction.DOWN) {
            ci.cancel();
            float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
            float f2 = Math.min(f1 * 6, 1.0F);
            this.walkAnimation.update(f2, 0.4F);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_livingTick(CallbackInfo ci) {
        if(hasSlowFallingFlag()){
            setSlowFallingFlag(false);
            addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0, false, false, false));
        }
        if(frostmintFreezingFlag){
            if(this.getTicksFrozen() > 0 && this.canFreeze()){
                if(!level().isClientSide && this.getTicksFrozen() > this.getTicksRequiredToFreeze() && level() instanceof ServerLevel serverLevel){
                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, this.getRandomX(1.0F), this.getRandomY(), this.getRandomZ(1.0F), 0, 0, 0, 0, 1D);
                }
            }else{
                frostmintFreezingFlag = false;
            }
        }
        boolean hasDarknessNow = this.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE);
        if (hadDarknessIncarnateEffect && !hasDarknessNow) {
            LivingEntity self = (LivingEntity) (Object) this;
            if (!self.level().isClientSide && self instanceof net.minecraft.server.level.ServerPlayer player) {
                if (!player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.getAbilities().setFlyingSpeed(0.05F);
                    player.onUpdateAbilities();
                    setSlowFallingFlag(true);
                }
            }
        }
        hadDarknessIncarnateEffect = hasDarknessNow;
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    protected void ac_increaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(ACEffectRegistry.BUBBLED)) {
            cir.setReturnValue(air);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;canBreatheUnderwater()Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_canBreatheUnderwater(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.DIVING_HELMET.get())
                || self.getVehicle() instanceof SubmarineEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "setLastHurtByMob",
            remap = true,
            cancellable = true,
            at =
            @At("TAIL")
    )
    private void ac_onSetLastHurtByMob(@Nullable net.minecraft.world.entity.LivingEntity attacker,
                                       CallbackInfo ci) {
        if (attacker instanceof net.minecraft.world.entity.raid.Raider atk) {
            var self = (net.minecraft.world.entity.LivingEntity) (Object) this;
            if (self instanceof net.minecraft.world.entity.raid.Raider || self instanceof net.minecraft.world.entity.monster.AbstractIllager) {
                if (isPossessed(atk)) {
                    if (self instanceof net.minecraft.world.entity.Mob mob) {
                        mob.setTarget(atk);
                    }
                }
            }
        }
    }

    public void setMagnetHeadRotation() {
        prevHeadYaw = this.getYHeadRot();
        prevHeadYaw0 = this.yHeadRotO;
        prevHeadPitch = this.getXRot();
        prevHeadPitch0 = this.xRotO;
        MagnetUtil.rotateHead((LivingEntity) (Entity) this);
    }


    public void resetMagnetHeadRotation() {
        this.yHeadRot = prevHeadYaw;
        this.yHeadRotO = prevHeadYaw0;
        setXRot(prevHeadPitch);
        this.xRotO = prevHeadPitch0;
    }

    public void setPossessedByWatcher(boolean possessedByWatcher){
        this.watcherPossessionFlag = possessedByWatcher;
    }

    public boolean isPossessedByWatcher(){
        return watcherPossessionFlag;
    }

    public void setSlowFallingFlag(boolean slowFallingFlag){
        this.slowFallingFlag = slowFallingFlag;
    }

    public boolean hasSlowFallingFlag(){
        return slowFallingFlag;
    }


    public void setFrostmintFreezing(boolean frostmintFreezingFlag){
        this.frostmintFreezingFlag = frostmintFreezingFlag;
    }
    public boolean isFreezingFromFrostmint(){
        return frostmintFreezingFlag;
    }

    @Unique
    private static boolean isPossessed(Entity e) {
        return e.getPersistentData().getBoolean("TotemPossessed");
    }
}
