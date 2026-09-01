package dev.proststuff.skinmatics.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.avatar.texture.AvatarTextureData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;

import java.util.function.Function;

public abstract class FaceLayer extends SkinmaticsRenderLayer {
    public static final float HEAD_SIZE = 0.25F;

    public FaceLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    public void start(PoseStack poseStack) {
        poseStack.pushPose();
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -HEAD_SIZE, -HEAD_SIZE);
    }

    public void translate(PoseStack poseStack, float offset) {
        poseStack.translate(0.0F, 0.0F, -offset);
    }

    public void stop(PoseStack poseStack) {
        poseStack.popPose();
    }

    public void renderTexture(PoseStack matrices, int order, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, RenderType renderType) {
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        int color = state.isSpectator ? ARGB.multiply(0x26FFFFFF, 0xFFFFFFFF) : 0xFFFFFFFF;

        submitNodeCollector.order(order).submitCustomGeometry(matrices, renderType, ((pose, vertexConsumer) -> {
            Matrix4f matrix = pose.pose();

            vertexConsumer
                    .addVertex(matrix, -HEAD_SIZE, -HEAD_SIZE, 0).setColor(color).setUv(0F,0F).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, -HEAD_SIZE, HEAD_SIZE, 0).setColor(color).setUv(0F,1F).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, HEAD_SIZE, HEAD_SIZE, 0).setColor(color).setUv(1F,1F).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, HEAD_SIZE, -HEAD_SIZE, 0).setColor(color).setUv(1F,0F).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1);
        }));
    }

    public void renderTexture(PoseStack matrices, int order, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, AvatarTextureData textureData, Function<Identifier, RenderType> renderTypeFactory) {
        if (textureData.isEmpty()) return;
        renderTexture(matrices, order, submitNodeCollector, lightCoords, state, renderTypeFactory.apply(textureData.id()));
    }
}
