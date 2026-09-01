package dev.proststuff.skinmatics.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarData;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.client.skinmatics.avatar.data.Eyes;
import dev.proststuff.skinmatics.client.skinmatics.avatar.texture.AvatarTextureData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jspecify.annotations.NonNull;

public class EyesLayer extends FaceLayer {
    public EyesLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public boolean shouldRenderWhenInvisible() {
        return true;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float yRot, float xRot) {
        AvatarTracker tracker = state.skinmatics$getTracker();
        if (tracker == null || ForceRenderLayer.isTrulyInvisible(state)) return;

        AvatarData avatarData = tracker.getAvatar();
        Eyes eyes = avatarData.eyes();

        start(poseStack);
        translate(poseStack, 0.001F);

        for (String group : eyes.textures().keySet()) {
            if (eyes.isHidden(group)) continue;
            String eyeState = tracker.getEyeState(group, state);
            AvatarTextureData textureData = eyes.get(group, eyeState, false).get(tracker);
            renderTexture(poseStack, 0, submitNodeCollector, lightCoords, state, textureData, RenderTypes::entityTranslucent);

            if (!eyes.isEmissive(group)) continue;
            AvatarTextureData emissiveResourceTextureData = eyes.get(group, eyeState, true).get(tracker);
            renderTexture(poseStack, 1, submitNodeCollector, lightCoords, state, emissiveResourceTextureData, avatarData::getEmissiveRenderType);
        }

        stop(poseStack);
    }
}