package dev.proststuff.skinmatics.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class EmissiveSkinLayer extends SkinmaticsRenderLayer {
    public EmissiveSkinLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public boolean shouldRenderWhenInvisible() {
        return true;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float limbAngle, float limbDistance) {
        if (getTracker(state) == null) return;
        if (ForceRenderLayer.isTrulyInvisible(state)) return;
        int color = state.isSpectator ? ARGB.multiply(0x26FFFFFF, 0xFFFFFFFF) : 0xFFFFFFFF;

        PlayerModel playerEntityModel = getParentModel();
        Profile profile = getProfile(state);
        TextureData textureData = profile != null ? profile.skin().getEmissiveTextures().get(profile, state) : TextureData.EMPTY;

        if (profile != null && !textureData.isEmpty()) submitNodeCollector.order(1).submitModel(playerEntityModel, state, poseStack, profile.getEmissiveRenderType(textureData.id()), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, null, state.outlineColor, null);
    }
}
