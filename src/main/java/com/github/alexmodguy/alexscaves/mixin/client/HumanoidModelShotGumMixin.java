package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.item.ShotGumItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidModel.class, priority = 1100)
public class HumanoidModelShotGumMixin {

    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseRightArm(LivingEntity entity, CallbackInfo ci) {
        if (!shouldPoseShotGum(entity)) {
            return;
        }
        boolean shotInRightHand = entity.getMainArm() == HumanoidArm.RIGHT;
    float aimXRot = head.xRot - (float) Math.toRadians(70F);
    float supportXRot = head.xRot - (float) Math.toRadians(55F);
    float aimYRot = head.yRot;
    float supportYRot = head.yRot + (float) Math.toRadians(shotInRightHand ? 50F : -50F);
        float supportZRot = (float) Math.toRadians(shotInRightHand ? 30F : -30F);
        if (shotInRightHand) {
            rightArm.xRot = aimXRot;
            rightArm.yRot = aimYRot;
            rightArm.zRot = 0.0F;
        } else {
            rightArm.xRot = supportXRot;
            rightArm.yRot = supportYRot;
            rightArm.zRot = supportZRot;
        }
        ci.cancel();
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
        if (!shouldPoseShotGum(entity)) {
            return;
        }
        boolean shotInRightHand = entity.getMainArm() == HumanoidArm.RIGHT;
    float aimXRot = head.xRot - (float) Math.toRadians(70F);
    float supportXRot = head.xRot - (float) Math.toRadians(55F);
    float aimYRot = head.yRot;
    float supportYRot = head.yRot + (float) Math.toRadians(shotInRightHand ? 50F : -50F);
        float supportZRot = (float) Math.toRadians(shotInRightHand ? 30F : -30F);
        if (shotInRightHand) {
            leftArm.xRot = supportXRot;
            leftArm.yRot = supportYRot;
            leftArm.zRot = supportZRot;
        } else {
            leftArm.xRot = aimXRot;
            leftArm.yRot = aimYRot;
            leftArm.zRot = 0.0F;
        }
        ci.cancel();
    }

    private boolean shouldPoseShotGum(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShotGumItem;
    }
}
