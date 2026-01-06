package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.MeltedCaramelEntity;
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

public class MeltedCaramelRenderer extends EntityRenderer<MeltedCaramelEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/caramel_cube/melted_caramel.png");

    public MeltedCaramelRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(MeltedCaramelEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        float despawnsIn = entity.getDespawnTime(partialTicks);
        float randomRotation = entity.getId() % 4 * 90;
        float randomYOffset = entity.getYRenderOffset();
        float despawnAlpha = Math.min(20F, despawnsIn) / 20F;
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        float alpha = despawnAlpha;
        poseStack.translate(0, randomYOffset, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(randomRotation));
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        vertex(vertexconsumer, matrix4f, posestack$pose, packedLight, 0.0F, 0, 0, 1, alpha);
        vertex(vertexconsumer, matrix4f, posestack$pose, packedLight, 1.0F, 0, 1, 1, alpha);
        vertex(vertexconsumer, matrix4f, posestack$pose, packedLight, 1.0F, 1, 1, 0, alpha);
        vertex(vertexconsumer, matrix4f, posestack$pose, packedLight, 0.0F, 1, 0, 0, alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, PoseStack.Pose pose, int p_253829_, float x, int y, int u, int v, float alpha) {
        vertexConsumer.addVertex(matrix4f, x - 0.5F, (float) 0.01F, y - 0.5F).setColor(1F, 1F, 1F, alpha).setUv((float) u, (float) v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(p_253829_).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public ResourceLocation getTextureLocation(MeltedCaramelEntity gumballEntity) {
        return TEXTURE;
    }
}
