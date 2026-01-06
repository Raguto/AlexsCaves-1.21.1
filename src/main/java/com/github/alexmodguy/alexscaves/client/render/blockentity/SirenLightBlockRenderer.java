package com.github.alexmodguy.alexscaves.client.render.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.SirenLightModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.block.SirenLightBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.SirenLightBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class SirenLightBlockRenderer<T extends SirenLightBlockEntity> implements BlockEntityRenderer<T> {

    private static final SirenLightModel MODEL = new SirenLightModel();
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/siren_light.png");
    private static final ResourceLocation COLOR_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/siren_light_color.png");


    public SirenLightBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(T light, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        BlockState state = light.getBlockState();
        Direction dir = state.getValue(SirenLightBlock.FACING);
        if (dir == Direction.UP) {
            poseStack.translate(0.5F, 1.5F, 0.5F);
        } else if (dir == Direction.DOWN) {
            poseStack.translate(0.5F, -0.5F, 0.5F);
        } else if (dir == Direction.NORTH) {
            poseStack.translate(0.5, 0.5F, -0.5F);
        } else if (dir == Direction.EAST) {
            poseStack.translate(1.5F, 0.5F, 0.5F);
        } else if (dir == Direction.SOUTH) {
            poseStack.translate(0.5, 0.5F, 1.5F);
        } else if (dir == Direction.WEST) {
            poseStack.translate(-0.5F, 0.5F, 0.5F);
        }
        poseStack.mulPose(dir.getOpposite().getRotation());
        int color = light.getColor();
        float r = (float) (color >> 16 & 255) / 255F;
        float g = (float) (color >> 8 & 255) / 255F;
        float b = (float) (color & 255) / 255F;
        float rotation = light.getSirenRotation(partialTicks);
        MODEL.setupAnim(null, rotation, 0, 0, 0, 0);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 0xFFFFFFFF);
        float f = light.getOnProgress(partialTicks);
        if (f > 0.0F) {
            float length = f * 1.25F;
            float width = f * f * 0.5F;
            poseStack.pushPose();
            poseStack.translate(0, 1.125F, 0);

            poseStack.mulPose(Axis.YP.rotationDegrees(rotation + 90));
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f1 = posestack$pose.pose();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getNucleeperLights());
            shineOriginVertex(lightConsumer, matrix4f1, posestack$pose, 0, 0, r, g, b);
            shineLeftCornerVertex(lightConsumer, matrix4f1, posestack$pose, length, width, 0, 0, r, g, b);
            shineRightCornerVertex(lightConsumer, matrix4f1, posestack$pose, length, width, 0, 0, r, g, b);
            shineLeftCornerVertex(lightConsumer, matrix4f1, posestack$pose, length, width, 0, 0, r, g, b);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            PoseStack.Pose posestack$pose2 = poseStack.last();
            Matrix4f matrix4f2 = posestack$pose2.pose();
            shineOriginVertex(lightConsumer, matrix4f2, posestack$pose2, 0, 0, r, g, b);
            shineLeftCornerVertex(lightConsumer, matrix4f2, posestack$pose2, length, width, 0, 0, r, g, b);
            shineRightCornerVertex(lightConsumer, matrix4f2, posestack$pose2, length, width, 0, 0, r, g, b);
            shineLeftCornerVertex(lightConsumer, matrix4f2, posestack$pose2, length, width, 0, 0, r, g, b);
            poseStack.popPose();

            poseStack.popPose();
        }
        int argb = 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        MODEL.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(COLOR_TEXTURE)), state.getValue(SirenLightBlock.POWERED) ? 240 : combinedLightIn, combinedOverlayIn, argb);
        poseStack.popPose();
    }

    private static void shineOriginVertex(VertexConsumer p_114220_, Matrix4f p_114221_, PoseStack.Pose pose, float xOffset, float yOffset, float r, float g, float b) {
        p_114220_.addVertex(p_114221_, 0.0F, 0.0F, 0.0F).setColor(r, g, b, 1).setUv(xOffset + 0.5F, yOffset).setOverlay(NO_OVERLAY).setLight(240).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void shineLeftCornerVertex(VertexConsumer p_114215_, Matrix4f p_114216_, PoseStack.Pose pose, float p_114217_, float p_114218_, float xOffset, float yOffset, float r, float g, float b) {
        p_114215_.addVertex(p_114216_, -ACMath.HALF_SQRT_3 * p_114218_, p_114217_, 0).setColor(r, g, b, 0).setUv(xOffset, yOffset + 1).setOverlay(NO_OVERLAY).setLight(240).setNormal(pose, 0.0F, -1.0F, 0.0F);
    }

    private static void shineRightCornerVertex(VertexConsumer p_114224_, Matrix4f p_114225_, PoseStack.Pose pose, float p_114226_, float p_114227_, float xOffset, float yOffset, float r, float g, float b) {
        p_114224_.addVertex(p_114225_, ACMath.HALF_SQRT_3 * p_114227_, p_114226_, 0).setColor(r, g, b, 0).setUv(xOffset + 1, yOffset + 1).setOverlay(NO_OVERLAY).setLight(240).setNormal(pose, 0.0F, -1.0F, 0.0F);
    }


    public int getViewDistance() {
        return 128;
    }
}
