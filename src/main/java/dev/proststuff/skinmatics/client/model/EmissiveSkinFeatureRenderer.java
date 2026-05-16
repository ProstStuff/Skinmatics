package dev.proststuff.skinmatics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.skinmatics.impl.SkinmaticsProfileHolder;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class EmissiveSkinFeatureRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public EmissiveSkinFeatureRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    public Profile getProfile(AvatarRenderState state) {
        SkinmaticsProfileHolder dataHolder = (SkinmaticsProfileHolder) state;
        return dataHolder.skinmatics$getProfile();
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float limbAngle, float limbDistance) {
        if (state.isInvisible) return;
        PlayerModel playerEntityModel = getParentModel();
        Profile profile = getProfile(state);

        Identifier texture = profile != null ? profile.data.getSkin(true) : null;
        if (texture != null) {
            submitNodeCollector.order(1).submitModel(playerEntityModel, state, poseStack, profile.getEmissiveRenderType(texture), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), state.outlineColor, null);
        }
    }
}
