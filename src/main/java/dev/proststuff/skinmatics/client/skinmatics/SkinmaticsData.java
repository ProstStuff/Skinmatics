package dev.proststuff.skinmatics.client.skinmatics;

import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkinmaticsData {
    public boolean enabled = true;
    public boolean strongerEmissiveGlow = false;
    public boolean customSkinEnabled = true;
    public boolean emissiveSkinEnabled = true;
    public boolean capeEnabled = true;
    public boolean customCapeEnabled = true;
    public boolean emissiveCapeEnabled = true;
    public boolean customElytraEnabled = true;
    public boolean emissiveElytraEnabled = true;
    public boolean eyesEnabled = true;
    public boolean overlayEnabled = true;
    public int maxTicks = -1;

    public boolean slim = false;
    public SkinmaticsLayerState skin = new SkinmaticsLayerState(2);
    public SkinmaticsLayerState cape = new SkinmaticsLayerState(2);
    public SkinmaticsLayerState elytra = new SkinmaticsLayerState(2);
    public SkinmaticsLayerState rightEye = new SkinmaticsLayerState(10);
    public SkinmaticsLayerState leftEye = new SkinmaticsLayerState(10);
    public SkinmaticsLayerState leftOverlay = new SkinmaticsLayerState(10);
    public SkinmaticsLayerState rightOverlay = new SkinmaticsLayerState(10);

    public int blinkingChance = 0;
    public int blinkingTicks = 0;
    public int holdBlinkingFor = 0;

    public void tick() {
        skin.tick(this);
        cape.tick(this);
        elytra.tick(this);
        rightEye.tick(this);
        rightOverlay.tick(this);
        leftEye.tick(this);
        leftOverlay.tick(this);

        if (blinkingTicks > 0) {
            blinkingTicks --;
        }
    }

    public boolean validateSkin() {
        return enabled && customSkinEnabled;
    }

    public boolean validateEmissiveSkin() {
        return enabled && emissiveSkinEnabled;
    }

    public boolean showCape() {
        return enabled && capeEnabled;
    }

    public boolean validateCustomCape() {
        return showCape() && customCapeEnabled;
    }

    public boolean validateEmissiveCape() {
        return showCape() && emissiveCapeEnabled;
    }

    public boolean validateElytra() {
        return (enabled && customElytraEnabled) || validateCustomCape();
    }

    public boolean validateEmissiveElytra() {
        return (enabled && customElytraEnabled && emissiveElytraEnabled) || validateEmissiveCape();
    }

    public Identifier getSkinTexture() {
        return skin.get();
    }

    public Identifier getEmissiveSkinTexture() {
        return skin.get(skin.ticks, 1);
    }

    public Identifier getCapeTexture() {
        return cape.get();
    }

    public Identifier getEmissiveCapeTexture() {
        return cape.get(cape.ticks, 1);
    }

    public Identifier getElytraTexture() {
        if (customElytraEnabled) {
            return elytra.get();
        } else {
            return getCapeTexture();
        }
    }

    public Identifier getEmissiveElytraTexture() {
        if (customElytraEnabled) {
            return elytra.get(elytra.ticks, 1);
        } else {
            return getEmissiveCapeTexture();
        }
    }

    public RenderLayer getEmissiveRenderLayer(Identifier texture) {
        return strongerEmissiveGlow ? RenderLayers.eyes(texture) : RenderLayers.entityTranslucentEmissiveNoOutline(texture);
    }

    public SkinmaticsLayerState getFaceLayerStateOf(EyeFeatureRenderer.EyePosition position, boolean overlay) {
        if (overlay) {
            return position == EyeFeatureRenderer.EyePosition.RIGHT ? rightOverlay : leftOverlay;
        } else {
            return position ==  EyeFeatureRenderer.EyePosition.RIGHT ? rightEye : leftEye;
        }
    }

    public Identifier getEyeTexture(EyeFeatureRenderer.EyePosition position) {
        return getFaceLayerStateOf(position, false).get();
    }

    public Identifier getOverlayTexture(EyeFeatureRenderer.EyePosition position) {
        return getFaceLayerStateOf(position, true).get();
    }
}
