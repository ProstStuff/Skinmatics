package dev.proststuff.skinmatics.avatar.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.Body;
import dev.proststuff.skinmatics.avatar.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.avatar.texture.AvatarTextureData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

public class EmissiveSkinRenderLayer extends SkinmaticsRenderLayer {
    public EmissiveSkinRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public boolean shouldRenderWhenInvisible() {
        return true;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float limbAngle, float limbDistance) {
        if (ForceRenderLayer.isTrulyInvisible(state)) return;
        AvatarTracker tracker = state.skinmatics$getTracker();

        if (tracker == null || tracker.isHidden(AvatarFeatureTypes.BODY)) return;
        AvatarFeatureData<Body> avatarFeatureData = tracker.getAvatar().getFeature(AvatarFeatureTypes.BODY);
        if (avatarFeatureData == null) return;
        Body settings = avatarFeatureData.settings();
        AvatarTextureData textureData = settings.emissiveTextures().get(tracker);

        if (!textureData.isEmpty()) {
            PlayerModel playerEntityModel = getParentModel();
            int color = state.isSpectator ? ARGB.multiply(0x26FFFFFF, 0xFFFFFFFF) : 0xFFFFFFFF;

            submitNodeCollector.order(1).submitModel(playerEntityModel, state, poseStack, AvatarUtils.getEmissiveRenderType(textureData.id(), settings.strongerEmissive()), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, null, state.outlineColor, null);
        }
    }
}
