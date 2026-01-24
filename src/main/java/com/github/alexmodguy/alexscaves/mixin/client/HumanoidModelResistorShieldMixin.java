package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.server.item.ResistorShieldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidModel.class, priority = 1100)
public class HumanoidModelResistorShieldMixin {

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Shadow
    public boolean crouching;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseRightArm(LivingEntity entity, CallbackInfo ci) {
        float useProgress = getUseProgressForArm(entity, HumanoidArm.RIGHT);
        if (useProgress <= 0.0F) {
            return;
        }
        float useProgressTurn = Math.min(useProgress * 4F, 1F);
        float useProgressUp = (float) Math.sin(useProgress * Math.PI);
        float armTilt = crouching ? 120F : 80F;
        rightArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
        rightArm.yRot = -(float) Math.toRadians(20F) * useProgressTurn;
        ci.cancel();
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
        float useProgress = getUseProgressForArm(entity, HumanoidArm.LEFT);
        if (useProgress <= 0.0F) {
            return;
        }
        float useProgressTurn = Math.min(useProgress * 4F, 1F);
        float useProgressUp = (float) Math.sin(useProgress * Math.PI);
        float armTilt = crouching ? 120F : 80F;
        leftArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
        leftArm.yRot = (float) Math.toRadians(20F) * useProgressTurn;
        ci.cancel();
    }

    private float getUseProgressForArm(LivingEntity entity, HumanoidArm arm) {
        if (!(entity instanceof Player player)) {
            return 0.0F;
        }
        ItemStack stack = getItemInArm(player, arm);
        if (!(stack.getItem() instanceof ResistorShieldItem)) {
            return 0.0F;
        }
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float useTime = ResistorShieldItem.getLerpedUseTime(stack, partialTick);
        return Math.min(10F, useTime) / 10F;
    }

    private ItemStack getItemInArm(Player player, HumanoidArm arm) {
        boolean mainArmMatches = player.getMainArm() == arm;
        InteractionHand hand = mainArmMatches ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return player.getItemInHand(hand);
    }
}
