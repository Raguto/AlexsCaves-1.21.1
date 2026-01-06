package com.github.alexmodguy.alexscaves.client.gui.book.widget;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class ImageWidget extends BookWidget {

    @Expose
    @SerializedName("image")
    private String image;
    @Expose
    private boolean sepia;
    @Expose
    private int u0;
    @Expose
    private int v0;
    @Expose
    private int u1;
    @Expose
    private int v1;
    @Expose
    private int width;
    @Expose
    private int height;

    @Expose(serialize = false, deserialize = false)
    private ResourceLocation actualTexture;


    public ImageWidget(int displayPage, String image, boolean sepia, int u0, int v0, int u1, int v1, int width, int height, int x, int y, float scale) {
        super(displayPage, Type.IMAGE, x, y, scale);
        this.image = image;
        this.sepia = sepia;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        this.width = width;
        this.height = height;
    }


    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTicks, boolean onFlippingPage) {
        if(this.actualTexture == null){
            this.actualTexture = ResourceLocation.parse(image);
        }
        VertexConsumer vertexconsumer = bufferSource.getBuffer(ACRenderTypes.getBookWidget(this.actualTexture, sepia));
        float scale = getScale();
        float scaledU0 = u0 / (float)width;
        float scaledU1 = u1 / (float)width;
        float scaledV0 = v0 / (float)height;
        float scaledV1 = v1 / (float)height;
        float texWidth = (u1 - u0) / 2F;
        float texHeight = (v1 - v0) / 2F;
        float alpha = 1.0F;
        Lighting.setupForFlatItems();

        poseStack.pushPose();
        poseStack.translate(getX(), getY(), -15);
        poseStack.scale(scale, scale, scale);
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        vertexconsumer.addVertex(matrix4f, texWidth, -texHeight, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(scaledU1, scaledV0).setOverlay(NO_OVERLAY).setLight(240).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexconsumer.addVertex(matrix4f, -texWidth, -texHeight, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(scaledU0, scaledV0).setOverlay(NO_OVERLAY).setLight(240).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexconsumer.addVertex(matrix4f, -texWidth, texHeight, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(scaledU0, scaledV1).setOverlay(NO_OVERLAY).setLight(240).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        vertexconsumer.addVertex(matrix4f, texWidth, texHeight, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setUv(scaledU1, scaledV1).setOverlay(NO_OVERLAY).setLight(240).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        poseStack.popPose();

    }
}
