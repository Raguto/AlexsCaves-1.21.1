package com.github.alexmodguy.alexscaves.mixin;


import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(
            method = {"Lnet/minecraft/world/entity/projectile/AbstractArrow;canHitEntity(Lnet/minecraft/world/entity/Entity;)Z"},
            cancellable = true,
            at = @At(value = "HEAD")
    )
    private void ac_canHitEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        AbstractArrow self = (AbstractArrow)(Object)this;
        if (self.getOwner() instanceof DeepOneBaseEntity && entity instanceof DeepOneBaseEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "TAIL")
    )
    private void ac_playerConstructor(EntityType<? extends AbstractArrow> arrowEntityType, LivingEntity shooter, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow)(Object)this;
        if (MagnetUtil.getEntityMagneticDirection(shooter) != Direction.DOWN) {
            self.setPos(shooter.getEyePosition().add(0, -0.1, 0));
        }
    }
}
