package dev.proststuff.skinmatics.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.v1.client.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileTracker;
import dev.proststuff.skinmatics.client.skinmatics.profile.data.Eyes;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
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
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState renderState, float yRot, float xRot) {
        ProfileTracker tracker = getTracker(renderState);
        if (tracker == null) return;
        if (ForceRenderLayer.isTrulyInvisible(renderState)) return;

        Profile profile = tracker.getProfile();
        Eyes eyes = profile.eyes();

        start(poseStack);
        translate(poseStack, 0.001F);

        for (String group : eyes.textures().keySet()) {
            if (eyes.isHidden(group)) continue;
            String state = tracker.getEyeState(group, renderState, true);
            TextureData textureData = eyes.get(group, state, false).get(profile, renderState);
            renderTexture(poseStack, 0, submitNodeCollector, lightCoords, renderState, textureData, RenderTypes::entityTranslucent);

            if (!eyes.isEmissive(group)) continue;
            TextureData emissiveTextureData = eyes.get(group, state, true).get(profile, renderState);
            renderTexture(poseStack, 1, submitNodeCollector, lightCoords, renderState, emissiveTextureData, profile::getEmissiveRenderType);
        }

        stop(poseStack);
    }
}