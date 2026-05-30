package dev.proststuff.skinmatics.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileData;
import dev.proststuff.skinmatics.api.client.ProfileRuntime;
import dev.proststuff.skinmatics.api.client.ProfileRuntimeManager;
import dev.proststuff.skinmatics.api.client.render.FaceRenderLayer;
import dev.proststuff.skinmatics.client.gui.customization.avatar.BodyPart;
import dev.proststuff.skinmatics.client.gui.customization.avatar.ModelPartHider;
import dev.proststuff.skinmatics.api.client.impl.ForceRenderLayer;
import dev.proststuff.skinmatics.api.client.impl.mixin.EntityPreviewer;
import dev.proststuff.skinmatics.api.client.impl.mixin.UUIDHolder;
import dev.proststuff.skinmatics.api.client.texture.LayerState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
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
        boolean headVisible = !isPreview || ModelPartHider.isVisible(BodyPart.HEAD);

        return enabled && headVisible && shown && !hidden;
    }

    @Override
    public boolean alwaysRender() {
        return true;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, @NonNull AvatarRenderState state, float yRot, float xRot) {
        if (ForceRenderLayer.isInvisible(state)) return;
        Profile profile = getProfile(state);
        if (!isEnabled(profile, state)) return;

        LayerState layerState = profile.data.getEyeLayerState(eyePosition);
        ProfileRuntime runtime = ProfileRuntimeManager.get(state);
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        ProfileRuntime.HasPose hasPose = ProfileRuntime.HasPose.EMPTY;

        if (level != null) {
            Player player = minecraft.level.getPlayerByUUID(((UUIDHolder) state).skinmatics$getUUID());

            if (player != null) {
                hasPose = player::hasPose;
            }
        }

        int eyeState = ProfileRuntime.getEyeState(state, runtime.isBlinking(), hasPose);

        start(poseStack, 0.001F);
        Identifier texture = layerState.getTexture(eyeState);
        if (texture != null) renderTexture(poseStack, 0, submitNodeCollector, lightCoords, state, RenderTypes.entityTranslucent(texture));

        if (profile.addEmissiveEyes.get()) {
            texture = layerState.getTexture(ProfileData.getEyeLayerStateEmissive(true) + eyeState);
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
