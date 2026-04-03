package dev.proststuff.skinmatics.client.model;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class FaceFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    protected SkinmaticsData cachedSkinmaticsData;

    public FaceFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);

    }

    protected abstract Identifier getTexture(PlayerEntityRenderState state);
    protected abstract float getFaceOffset(PlayerEntityRenderState state);
    public abstract boolean isEnabled();
    protected abstract RenderLayer getRenderLayer(Identifier texture);

    public SkinmaticsData getSkinmatics(PlayerEntityRenderState state) {
        if (cachedSkinmaticsData == null) {
            cachedSkinmaticsData = SkinmaticsClient.getSkinmaticsFromRenderState(state);
        }

        return cachedSkinmaticsData;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (state.invisible) return;
        getSkinmatics(state);
        if (!isEnabled()) return;
        if (getContextModel().head == null) return;
        Identifier texture = getTexture(state);
        if (texture == null) return;

        matrices.push();
        getContextModel().head.applyTransform(matrices);
        matrices.translate(0.0F, -0.25F, -(0.25F + getFaceOffset(state)));
        renderFaceQuad(matrices, queue, texture, light, LivingEntityRenderer.getOverlay(state, 0.0F));
        matrices.pop();
    }

    private void renderFaceQuad(MatrixStack matrices, OrderedRenderCommandQueue queue, Identifier texture, int light, int overlay) {
        float size = 0.25F;

        queue.submitCustom(matrices, getRenderLayer(texture), ((matricesEntry, vertexConsumer) -> {
            Matrix4f matrix = matricesEntry.getPositionMatrix();

            vertexConsumer
                    .vertex(matrix, -size, -size, 0).color(255,255,255,255).texture(0f,0f).overlay(overlay).light(light).normal(matricesEntry, 0, 0, -1)
                    .vertex(matrix, -size, size, 0).color(255,255,255,255).texture(0f,1f).overlay(overlay).light(light).normal(matricesEntry, 0, 0, -1)
                    .vertex(matrix, size, size, 0).color(255,255,255,255).texture(1f,1f).overlay(overlay).light(light).normal(matricesEntry, 0, 0, -1)
                    .vertex(matrix, size, -size, 0).color(255,255,255,255).texture(1f,0f).overlay(overlay).light(light).normal(matricesEntry, 0, 0, -1);
        }));
    }
}
