package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SugarStaffHexRenderer extends EntityRenderer<SugarStaffHexEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sugar_staff_hex.png");

    public SugarStaffHexRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(SugarStaffHexEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
        poseStack.pushPose();
        float despawnsIn = entity.getDespawnTime(partialTicks);
        float randomRotation = entity.getId() % 4 * 90;
        float randomYOffset = entity.getYRenderOffset();
        float tickCount = entity.tickCount + partialTicks;
        float minAge = Math.min(1F, Math.min(tickCount, despawnsIn) / 10F);
        float alpha = minAge;
        float alpha2 = alpha * alpha;
        float scale = 4.0F * entity.getHexScale();
        poseStack.translate(0, randomYOffset, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(randomRotation + 3.0F * tickCount));
        poseStack.scale(scale, scale, scale);
        renderHex(poseStack, multiBufferSource, ACRenderTypes.getPurpleWitch(TEXTURE), 1F, 1F, 1F - alpha2, 1F);
        for(int i = 0; i < 5; i++){
            float f = (1F - i / 5F) * 0.5F;
            float bob = (float) (Math.sin(tickCount * 0.2F + i) * 0.005F);
            renderHex(poseStack, multiBufferSource, ACRenderTypes.getVoidBeingCloud(TEXTURE), 1 * f, 1F, 1F - alpha2 * f, 1F);
            poseStack.translate(0, 0.01F + bob + (1F - alpha2) * 0.03F, 0);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    public static void renderHex(PoseStack poseStack, MultiBufferSource multiBufferSource, RenderType renderType, float alpha, float r, float g, float b){
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(renderType);
        vertex(vertexconsumer, matrix4f, posestack$pose, 240, 0.0F, 0, 0, 1, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, posestack$pose, 240, 1.0F, 0, 1, 1, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, posestack$pose, 240, 1.0F, 1, 1, 0, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, posestack$pose, 240, 0.0F, 1, 0, 0, alpha, r, g, b);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, PoseStack.Pose pose, int p_253829_, float x, int y, int u, int v, float alpha, float r, float g, float b) {
        vertexConsumer.addVertex(matrix4f, x - 0.5F, (float) 0.01F, y - 0.5F).setColor(r, g, b, alpha).setUv((float) u, (float) v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(p_253829_).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public ResourceLocation getTextureLocation(SugarStaffHexEntity gumballEntity) {
        return TEXTURE;
    }
}
