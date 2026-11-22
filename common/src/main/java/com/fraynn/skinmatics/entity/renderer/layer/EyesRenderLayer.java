package com.fraynn.skinmatics.entity.renderer.layer;

import com.fraynn.skinmatics.avatar.extension.AvatarTrackedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.AvatarUtils;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.runtime.AvatarRuntimeTypes;
import com.fraynn.skinmatics.avatar.feature.runtime.EyesAvatarRuntime;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.eye.Eyes;
import com.fraynn.skinmatics.entity.renderer.ForceRenderLayer;
import com.fraynn.skinmatics.texture.AvatarTextureData;
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
        AvatarTracker tracker = ((AvatarTrackedEntity) state).skinmatics$getTracker();
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