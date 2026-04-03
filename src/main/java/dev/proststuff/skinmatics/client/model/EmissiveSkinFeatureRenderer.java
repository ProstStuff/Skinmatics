package dev.proststuff.skinmatics.client.model;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EmissiveSkinFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    protected SkinmaticsData cachedSkinmaticsData;

    public EmissiveSkinFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    public SkinmaticsData getSkinmatics(PlayerEntityRenderState state) {
        if (cachedSkinmaticsData == null) {
            cachedSkinmaticsData = SkinmaticsClient.getSkinmaticsFromRenderState(state);
        }

        return cachedSkinmaticsData;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (state.invisible) return;
        PlayerEntityModel playerEntityModel = getContextModel();
        SkinmaticsData data = getSkinmatics(state);

        if (data != null && data.validateEmissiveSkin() && data.getEmissiveSkinTexture() != null) {
            queue.getBatchingQueue(1).submitModel(playerEntityModel, state, matrices, data.getEmissiveRenderLayer(data.getEmissiveSkinTexture()), light, LivingEntityRenderer.getOverlay(state, 0.0F), state.outlineColor, null);
        }
    }
}
