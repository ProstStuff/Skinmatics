package dev.proststuff.skinmatics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.impl.ForceRenderLayer;
import dev.proststuff.skinmatics.api.client.impl.ProfileHolder;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class EmissiveSkinRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> implements ForceRenderLayer {
    public EmissiveSkinRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public boolean alwaysRender() {
        return true;
    }

    public Profile getProfile(AvatarRenderState state) {
        ProfileHolder dataHolder = (ProfileHolder) state;
        return dataHolder.skinmatics$getProfile();
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float limbAngle, float limbDistance) {
        if (ForceRenderLayer.isInvisible(state)) return;
        int color = state.isSpectator ? ARGB.multiply(654311423, 0xFFFFFFFF) : 0xFFFFFFFF;

        PlayerModel playerEntityModel = getParentModel();
        Profile profile = getProfile(state);

        Identifier texture = profile != null ? profile.data.getSkin(true) : null;
        if (texture != null) {
            submitNodeCollector.order(1).submitModel(playerEntityModel, state, poseStack, profile.getEmissiveRenderType(texture), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, null, state.outlineColor, null);
        }
    }
}
