package dev.proststuff.skinmatics.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.client.avatar.AvatarTracker;
import dev.proststuff.skinmatics.client.avatar.texture.AvatarTextureData;
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
        if (ForceRenderLayer.isTrulyInvisible(state)) return;
        AvatarTracker tracker = state.skinmatics$getTracker();
        if (tracker != null && tracker.getAvatar().skin().isEmissive()) {
            AvatarTextureData textureData = tracker.getAvatar().skin().getEmissiveTextures().get(tracker);

            if (!textureData.isEmpty()) {
                PlayerModel playerEntityModel = getParentModel();
                int color = state.isSpectator ? ARGB.multiply(0x26FFFFFF, 0xFFFFFFFF) : 0xFFFFFFFF;

                submitNodeCollector.order(1).submitModel(playerEntityModel, state, poseStack, tracker.getAvatar().getEmissiveRenderType(textureData.id()), lightCoords, LivingEntityRenderer.getOverlayCoords(state, 0.0F), color, null, state.outlineColor, null);
            }
        }
    }
}
