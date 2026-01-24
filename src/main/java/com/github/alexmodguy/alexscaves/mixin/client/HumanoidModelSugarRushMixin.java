package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelSugarRushMixin {

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Shadow
    public ModelPart rightLeg;

    @Shadow
    public ModelPart leftLeg;

    @Shadow
    public ModelPart head;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void ac_setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player)) {
            return;
        }
        var sugarRush = ACEffectRegistry.SUGAR_RUSH.getDelegate();
        if (sugarRush == null || !player.hasEffect(sugarRush) || AlexsCaves.PROXY.isFirstPersonPlayer(player)) {
            return;
        }
        float speedModifier = 0.35F;
        if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get() && AlexsCaves.PROXY.isTickRateModificationActive(Minecraft.getInstance().level)) {
            float tickRate = ClientTickRateTracker.getForClient(Minecraft.getInstance()).getClientTickRate() / 50.0F;
            speedModifier *= tickRate;
        }
        float deltaSpeed = 1.0F;
        float partialTicks = AlexsCaves.PROXY.getPartialTicks();
        float walkPos = player.walkAnimation.position(partialTicks);
        float walkSpeed = player.walkAnimation.speed(partialTicks);
        float headXRot = player.getViewXRot(partialTicks);
        float headYRot = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot) - Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        rightArm.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI * 0.5F) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
        leftArm.xRot = Mth.cos(walkPos * speedModifier) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
        rightArm.zRot = (Mth.sin(walkPos * -speedModifier + (float) Math.PI * 0.5F) + 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
        leftArm.zRot = (Mth.sin(walkPos * -speedModifier) - 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
        head.xRot = headXRot * ((float) Math.PI / 180F) + Mth.cos(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
        head.yRot = headYRot * ((float) Math.PI / 180F) + Mth.sin(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
        leftLeg.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
        rightLeg.xRot = Mth.cos(walkPos * speedModifier) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
        float flailTime = (player.tickCount + partialTicks) * speedModifier;
        float armFlail = Mth.sin(flailTime * 0.9F) * 0.7F;
        float legFlail = Mth.cos(flailTime * 0.9F) * 0.5F;
        rightArm.xRot += armFlail;
        leftArm.xRot -= armFlail;
        rightArm.zRot += armFlail * 0.35F;
        leftArm.zRot -= armFlail * 0.35F;
        rightLeg.xRot -= legFlail;
        leftLeg.xRot += legFlail;
    }
}
