package dev.proststuff.skinmatics.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.impl.ForceRenderLayer;
import dev.proststuff.skinmatics.api.client.impl.mixin.ProfileHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public abstract class FaceRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> implements ForceRenderLayer {
    public static final float HEAD_SIZE = 0.25F;

    public FaceRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    public boolean alwaysRender() {
        return false;
    }

    public abstract void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float limbAngle, float limbDistance);

    public Profile getProfile(AvatarRenderState state) {
        ProfileHolder dataHolder = (ProfileHolder) state;
        return dataHolder.skinmatics$getProfile();
    }

    public void start(PoseStack poseStack, float offset) {
        poseStack.pushPose();
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -HEAD_SIZE, -HEAD_SIZE);
        translate(poseStack, offset);
    }

    public void translate(PoseStack poseStack, float offset) {
        poseStack.translate(0.0F, 0.0F, -offset);
    }

    public void stop(PoseStack poseStack) {
        poseStack.popPose();
    }

    public void renderTexture(PoseStack matrices, int order, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, RenderType renderType) {
        int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
        int color = state.isSpectator ? ARGB.multiply(654311423, 0xFFFFFFFF) : 0xFFFFFFFF;

        submitNodeCollector.order(order).submitCustomGeometry(matrices, renderType, ((pose, vertexConsumer) -> {
            Matrix4f matrix = pose.pose();

            vertexConsumer
                    .addVertex(matrix, -HEAD_SIZE, -HEAD_SIZE, 0).setColor(color).setUv(0f,0f).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, -HEAD_SIZE, HEAD_SIZE, 0).setColor(color).setUv(0f,1f).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, HEAD_SIZE, HEAD_SIZE, 0).setColor(color).setUv(1f,1f).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1)
                    .addVertex(matrix, HEAD_SIZE, -HEAD_SIZE, 0).setColor(color).setUv(1f,0f).setOverlay(overlay).setLight(lightCoords).setNormal(pose, 0, 0, -1);
        }));
    }
}
