package dev.proststuff.skinmatics.avatar.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.AvatarUtils;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntimeTypes;
import dev.proststuff.skinmatics.avatar.feature.runtime.EyesAvatarRuntime;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.eye.Eyes;
import dev.proststuff.skinmatics.avatar.renderer.ForceRenderLayer;
import dev.proststuff.skinmatics.avatar.texture.AvatarTextureData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.jspecify.annotations.NonNull;

public class EyesRenderLayer extends FaceRenderLayer {
    public EyesRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float yRot, float xRot) {
        AvatarTracker tracker = state.skinmatics$getTracker();
        if (tracker == null || tracker.isHidden(AvatarFeatureTypes.EYES) || ForceRenderLayer.isTrulyInvisible(state)) return;

        AvatarProfile avatarProfile = tracker.getAvatar();
        EyesAvatarRuntime runtime = tracker.getRuntime(AvatarRuntimeTypes.EYES);
        AvatarFeatureData<Eyes> featureData = avatarProfile.getFeature(AvatarFeatureTypes.EYES);
        if (featureData == null || runtime == null) return;
        Eyes settings = featureData.settings();

        start(poseStack);
        translate(poseStack, 0.001F);

        for (String eyeGroup : settings.baseTextures().keySet()) {
            if (settings.isHidden(eyeGroup)) continue;

            String eyeState = runtime.getEyeState(eyeGroup, state);
            AvatarTextureData textureData = settings.baseTextures().get(eyeGroup).get(eyeState).get(tracker);
            renderTexture(poseStack, 0, submitNodeCollector, lightCoords, state, textureData, RenderTypes::entityTranslucent);

            if (!settings.isEmissive(eyeGroup)) continue;
            AvatarTextureData emissiveResourceTextureData = settings.emissiveTextures().get(eyeGroup).get(eyeState).get(tracker);
            renderTexture(poseStack, 1, submitNodeCollector, lightCoords, state, emissiveResourceTextureData, (id) -> AvatarUtils.getEmissiveRenderType(id, settings.strongerEmissive().contains(eyeGroup)));
        }

        stop(poseStack);
    }
}