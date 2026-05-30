package dev.proststuff.skinmatics.api.client;

import dev.proststuff.skinmatics.client.render.EyeRenderLayer;
import dev.proststuff.skinmatics.api.client.render.SkinmaticsRenderTypes;
import dev.proststuff.skinmatics.api.client.texture.LayerState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * A texture preloader and a ticker. Textures are applied to every (assigned) entity. Exists per profile.
 */
public class ProfileData {
    public final Profile profile;
    public final LayerState skin = new LayerState(2);
    public final LayerState cape = new LayerState(2);
    public final LayerState wings = new LayerState(2);
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
        wings.tick(ticks);

        if (profile.showEyes.get()) {
            rightEye.tick(ticks);
            leftEye.tick(ticks);
        }
    }

    public void clear() {
        ticks = 0;

        skin.clear();
        cape.clear();
        wings.clear();
        rightEye.clear();
        leftEye.clear();
    }

    public Identifier getSkin(boolean emissive) {
        return profile.enabled.get() && (emissive ? profile.addEmissiveSkin.get() : profile.useCustomSkin.get()) ? skin.getTexture(emissive ? 1 : 0) : null;
    }

    public Identifier getCape(boolean emissive) {
        return profile.enabled.get() && profile.showCape.get() && (emissive ? profile.addEmissiveCape.get() : profile.useCustomCape.get()) ? cape.getTexture(emissive ? 1 : 0) : null;
    }

    public Identifier getWings(boolean emissive) {
        return profile.enabled.get() && (emissive ? profile.addEmissiveWings.get() : profile.useCustomWings.get()) ? wings.getTexture(emissive ? 1 : 0) : getCape(emissive);
    }

    public LayerState getEyeLayerState(EyeRenderLayer.EyePosition eyePosition) {
        return eyePosition == EyeRenderLayer.EyePosition.RIGHT ? rightEye : leftEye;
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return profile.strongerEmissiveGlow.get() ? SkinmaticsRenderTypes.eyesNoCull(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }

    public static int getEyeLayerStateEmissive(boolean emissive) {
        return emissive ? 10 : 0;
    }
}
