package dev.proststuff.skinmatics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.impl.EntityPreviewer;
import dev.proststuff.skinmatics.api.client.render.FaceRenderLayer;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarMinimap;
import dev.proststuff.skinmatics.client.gui.element.avatar.BodyPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class EyeRenderLayer extends FaceRenderLayer {
    public final EyePosition eyePosition;

    public EyeRenderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> context, EyePosition eyePosition) {
        super(context);
        this.eyePosition = eyePosition;
    }

    public boolean isEnabled(Profile profile, AvatarRenderState state) {
        if (profile == null) {
            return false;
        }

        boolean enabled = profile.enabled.get();
        boolean shown = profile.showEyes.get();
        boolean hidden = (eyePosition == EyePosition.RIGHT ? profile.hideRightEye : profile.hideLeftEye).get();
        boolean isPreview = ((EntityPreviewer) state).skinmatics$isPreviewing();
        boolean headVisible = !isPreview || AvatarMinimap.isVisible(BodyPart.HEAD);

        return enabled && headVisible && shown && !hidden;
    }

    @Override
    public boolean alwaysRender() {
        return true;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float yRot, float xRot) {
        if (state.isInvisible && !state.isSpectator) return;
        Profile profile = getProfile(state);
        if (!isEnabled(profile, state)) return;

        start(poseStack, 0.001F);
        Identifier texture = profile.data.getEye(state, eyePosition, false);
        if (texture != null) renderTexture(poseStack, 0, submitNodeCollector, lightCoords, state, RenderTypes.entityTranslucent(texture));

        if (profile.addEmissiveEyes.get()) {
            texture = profile.data.getEye(state, eyePosition, true);
            if (texture != null) renderTexture(poseStack, 1, submitNodeCollector, lightCoords, state, profile.getEmissiveRenderType(texture));
        }

        stop(poseStack);
    }

    public enum EyeDirection {
        CLOSED("closed"),
        FRONT("front"),
        UP("up"),
        DOWN("down"),

        RIGHT("right"),
        RIGHT_UP("rightUp"),
        RIGHT_DOWN("rightDown"),

        LEFT("left"),
        LEFT_UP("leftUp"),
        LEFT_DOWN("leftDown"),;

        final String serializedName;

        EyeDirection(String serializedName) {
            this.serializedName = serializedName;
        }

        public String getSerializedName() {
            return serializedName;
        }

        public static EyeDirection fromOrdinal(int ordinal) {
            return values()[ordinal];
        }

        public static EyeDirection fromSerializedName(String serializedName) {
            for (EyeDirection eyeDirection : values()) {
                if (eyeDirection.getSerializedName().equals(serializedName)) {
                    return eyeDirection;
                }
            }

            return EyeDirection.CLOSED;
        }
    }

    public enum EyePosition {
        LEFT,
        RIGHT
    }
}
