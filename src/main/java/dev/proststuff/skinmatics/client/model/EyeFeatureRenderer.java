package dev.proststuff.skinmatics.client.model;

import com.google.gson.annotations.SerializedName;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsLayerState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EyeFeatureRenderer extends FaceFeatureRenderer {
    public final EyePosition EYE_POSITION;
    public final boolean OVERLAY;

    protected EyeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EyePosition eyePosition, boolean overlay) {
        super(context);
        this.EYE_POSITION = eyePosition;
        this.OVERLAY = overlay;
    }

    public EyeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EyePosition eyePosition) {
        this(context, eyePosition, false);
    }

    @Override
    protected Identifier getTexture(PlayerEntityRenderState state) {
        SkinmaticsData skinmatics = getSkinmatics(state);
        return skinmatics.getEyeTexture(EYE_POSITION);
    }

    @Override
    protected float getFaceOffset(PlayerEntityRenderState state) {
        return 0.01F;
    }

    @Override
    protected RenderLayer getRenderLayer(Identifier texture) {
        if (cachedSkinmaticsData == null) return RenderLayers.entityCutout(texture);
        SkinmaticsLayerState skinmaticsLayerState = cachedSkinmaticsData.getFaceLayerStateOf(EYE_POSITION, OVERLAY);
        return skinmaticsLayerState.getRenderLayer(texture, cachedSkinmaticsData);
    }

    @Override
    public boolean isEnabled() {
        return cachedSkinmaticsData != null && cachedSkinmaticsData.enabled && cachedSkinmaticsData.eyesEnabled;
    }

    public enum EyeDirection {
        @SerializedName("closed") CLOSED,
        @SerializedName("front") FRONT,
        @SerializedName("up") UP,
        @SerializedName("down") DOWN,

        @SerializedName("right") RIGHT,
        @SerializedName("rightUp") RIGHT_UP,
        @SerializedName("rightDown") RIGHT_DOWN,

        @SerializedName("left") LEFT,
        @SerializedName("leftUp") LEFT_UP,
        @SerializedName("leftDown") LEFT_DOWN;

        public static EyeDirection fromOrdinal(int ordinal) {
            return values()[ordinal];
        }
    }

    public enum EyePosition {
        @SerializedName("left") LEFT,
        @SerializedName("right") RIGHT
    }
}
