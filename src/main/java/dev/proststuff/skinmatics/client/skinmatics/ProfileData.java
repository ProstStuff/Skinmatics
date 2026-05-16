package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;


public class ProfileData {
    public final Profile profile;
    public final LayerState skin = new LayerState(2);
    public final LayerState cape = new LayerState(2);
    public final LayerState elytra = new LayerState(2);
    public final LayerState rightEye = new LayerState(20);
    public final LayerState leftEye = new LayerState(20);

    public int ticks = 0;

    public ProfileData(Profile profile) {
        this.profile = profile;
    }

    public void tick() {
        int max = Math.max(1, profile.maxTicks.get());
        ticks = (ticks + 1) % max;

        skin.tick(ticks);
        cape.tick(ticks);
        elytra.tick(ticks);

        if (profile.showEyes.get()) {
            rightEye.tick(ticks);
            leftEye.tick(ticks);
        }
    }

    public Identifier getSkin(boolean emissive) {
        return profile.enabled.get() && (emissive ? profile.addEmissiveSkin.get() : profile.useCustomSkin.get()) ? skin.getTexture(emissive ? 1 : 0) : null;
    }

    public Identifier getCape(boolean emissive) {
        return profile.enabled.get() && profile.showCape.get() && (emissive ? profile.addEmissiveCape.get() : profile.useCustomCape.get()) ? cape.getTexture(emissive ? 1 : 0) : null;
    }

    public Identifier getElytra(boolean emissive) {
        return profile.enabled.get() && (emissive ? profile.addEmissiveElytra.get() : profile.useCustomElytra.get()) ? elytra.getTexture(emissive ? 1 : 0) : getCape(emissive);
    }

    public LayerState getEyeLayerState(EyeFeatureRenderer.EyePosition eyePosition) {
        return eyePosition == EyeFeatureRenderer.EyePosition.RIGHT ? rightEye : leftEye;
    }

    public Identifier getEye(AvatarRenderState state, EyeFeatureRenderer.EyePosition eyePosition, boolean emissive) {
        ProfileRuntime runtime = ProfileHandler.getRuntime(state);
        return profile.enabled.get() && runtime != null ? getEyeLayerState(eyePosition).getTexture(runtime.getEyeLayerStateOffset(eyePosition, emissive)) : null;
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return profile.strongerEmissiveGlow.get() ? SkinmaticsRenderTypes.eyesNoCull(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }

    public static int getEyeLayerStateEmissive(boolean emissive) {
        return emissive ? 10 : 0;
    }
}
