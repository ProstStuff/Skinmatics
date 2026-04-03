package dev.proststuff.skinmatics.client.model;

import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * An {@link EyeFeatureRenderer} subclass for non-emissive texture in-case {@link EyeFeatureRenderer} has emissive.
 */
@Environment(EnvType.CLIENT)
public class EyeOverlayFeatureRenderer extends EyeFeatureRenderer {
    public EyeOverlayFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, EyePosition eyePosition) {
        super(context, eyePosition, true);
    }

    @Override
    protected Identifier getTexture(PlayerEntityRenderState state) {
        SkinmaticsData skinmatics = getSkinmatics(state);
        return skinmatics.getOverlayTexture(EYE_POSITION);
    }

    @Override
    protected float getFaceOffset(PlayerEntityRenderState state) {
        return 0.015F;
    }

    @Override
    public boolean isEnabled() {
        return cachedSkinmaticsData != null && cachedSkinmaticsData.enabled && cachedSkinmaticsData.overlayEnabled;
    }
}
