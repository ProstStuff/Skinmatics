package dev.proststuff.skinmatics.client.skinmatics;

import com.google.gson.annotations.Expose;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import dev.proststuff.skinmatics.client.utility.SkinmaticsJsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class PersistentSkinmaticsData {
    @Expose public boolean enabled = true;
    @Expose public int maxTicks = 0;
    @Expose public boolean strongerEmissiveGlow = false;

    @Expose public boolean enableCustomSkin = false;
    @Expose public boolean enableEmissiveSkin = false;
    @Expose public boolean showCape = true;
    @Expose public boolean enableCustomCape = false;
    @Expose public boolean enableEmissiveCape = false;
    @Expose public boolean enableCustomElytra = false;
    @Expose public boolean enableEmissiveElytra = false;
    public boolean slim = false; // wip

    @Expose public boolean enableEyes = false;
    @Expose public int blinkingChancePerTick = 48;
    @Expose public int holdBlinkingForTicks = 4;
    @Expose public boolean enableEmissiveRightEye = false;
    @Expose public boolean enableEmissiveLeftEye = false;
    @Expose public boolean enableOverlays = false;

    @Expose public Animatable skin = new Animatable();
    @Expose public Animatable emissiveSkin = new Animatable();
    @Expose public Animatable cape = new Animatable();
    @Expose public Animatable emissiveCape = new Animatable();
    @Expose public Animatable elytra = new Animatable();
    @Expose public Animatable emissiveElytra = new Animatable();
    @Expose public HashMap<EyeFeatureRenderer.EyePosition, Map<EyeFeatureRenderer.EyeDirection, Animatable>> eyes = new HashMap<>();
    @Expose public HashMap<EyeFeatureRenderer.EyePosition, Map<EyeFeatureRenderer.EyeDirection, Animatable>> overlays = new HashMap<>();

    public PersistentSkinmaticsData() {
        for (EyeFeatureRenderer.EyePosition eyePosition : EyeFeatureRenderer.EyePosition.values()) {
            HashMap<EyeFeatureRenderer.EyeDirection, Animatable> animatables = new LinkedHashMap<>();

            for (EyeFeatureRenderer.EyeDirection eyeDirection : EyeFeatureRenderer.EyeDirection.values()) {
                animatables.put(eyeDirection, new Animatable());
            }

            eyes.put(eyePosition, animatables);
            overlays.put(eyePosition, (HashMap<EyeFeatureRenderer.EyeDirection, Animatable>) animatables.clone());
        }
    }

    private String profile;

    public void save() {
        if (!profile.equals(SkinmaticsClient.CONFIG.getProfile())) return;

        try {
            Path dir = SkinmaticsJsonUtils.getModPath();
            if (!Files.exists(dir)) Files.createDirectories(dir.resolve("textures"));

            SkinmaticsJsonUtils.write(SkinmaticsClient.CONFIG.getProfile(), dir.resolve("profiles"), this);
        } catch (IOException e) {
            SkinmaticsClient.LOGGER.error("Unable to save local skinmatics.", e);
        }
    }

    public static PersistentSkinmaticsData load() {
        try {
            String profile = SkinmaticsClient.CONFIG.getProfile();
            PersistentSkinmaticsData data = SkinmaticsJsonUtils.read(profile, SkinmaticsJsonUtils.getModPath().resolve("profiles"), PersistentSkinmaticsData.class, PersistentSkinmaticsData::new);
            data.profile = profile;
            return data;
        }  catch (IOException e) {
            SkinmaticsClient.LOGGER.warn("Unable to load local player Skinmatics.", e);
            return new PersistentSkinmaticsData();
        }
    }

    public void apply(SkinmaticsData skinmaticsData) {
        skinmaticsData.skin.clear();
        skinmaticsData.cape.clear();
        skinmaticsData.elytra.clear();
        skinmaticsData.rightEye.clear();
        skinmaticsData.rightOverlay.clear();
        skinmaticsData.leftEye.clear();
        skinmaticsData.leftOverlay.clear();

        skinmaticsData.enabled = enabled;
        skinmaticsData.strongerEmissiveGlow = strongerEmissiveGlow;
        skinmaticsData.customSkinEnabled = enableCustomSkin;
        skinmaticsData.emissiveSkinEnabled = enableEmissiveSkin;
        skinmaticsData.capeEnabled = showCape;
        skinmaticsData.customCapeEnabled = enableCustomCape;
        skinmaticsData.emissiveCapeEnabled = enableEmissiveCape;
        skinmaticsData.customElytraEnabled = enableCustomElytra;
        skinmaticsData.emissiveElytraEnabled = enableEmissiveElytra;
        skinmaticsData.eyesEnabled = enableEyes;
        skinmaticsData.overlayEnabled = enableOverlays;

        skinmaticsData.maxTicks = maxTicks;
        skinmaticsData.blinkingChance = blinkingChancePerTick;
        skinmaticsData.holdBlinkingFor = holdBlinkingForTicks;

        skinmaticsData.slim = slim;

        loadTextures(skin, (tick, texture) -> skinmaticsData.skin.getState(0).put(tick, texture));
        loadTextures(emissiveSkin, (tick, texture) -> skinmaticsData.skin.getState(1).put(tick, texture));
        loadTextures(cape, (tick, texture) -> skinmaticsData.cape.getState(0).put(tick, texture));
        loadTextures(emissiveCape, (tick, texture) -> skinmaticsData.cape.getState(1).put(tick, texture));
        loadTextures(elytra, (tick, texture) -> skinmaticsData.elytra.getState(0).put(tick, texture));
        loadTextures(emissiveElytra, (tick, texture) -> skinmaticsData.elytra.getState(1).put(tick, texture));

        for (EyeFeatureRenderer.EyePosition eyePosition : EyeFeatureRenderer.EyePosition.values()) {
            Map<EyeFeatureRenderer.EyeDirection, Animatable> eye = eyes.getOrDefault(eyePosition, null);

            if (eye != null) {
                SkinmaticsLayerState skinmaticsLayerState = skinmaticsData.getFaceLayerStateOf(eyePosition, false);

                for (int state = 0; state < 10; state++) {
                    EyeFeatureRenderer.EyeDirection eyeDirection = EyeFeatureRenderer.EyeDirection.fromOrdinal(state);

                    if (eye.containsKey(eyeDirection)) {
                        int s = state;
                        loadTextures(eye.get(eyeDirection), (tick, texture) -> skinmaticsLayerState.getState(s).put(tick, texture));
                    }
                }
            }

            Map<EyeFeatureRenderer.EyeDirection, Animatable> overlay = overlays.getOrDefault(eyePosition, null);

            if (overlay != null) {
                SkinmaticsLayerState skinmaticsLayerState = skinmaticsData.getFaceLayerStateOf(eyePosition, true);

                for (int state = 0; state < 10; state++) {
                    EyeFeatureRenderer.EyeDirection eyeDirection = EyeFeatureRenderer.EyeDirection.fromOrdinal(state);

                    if (overlay.containsKey(eyeDirection)) {
                        int s = state;
                        loadTextures(overlay.get(eyeDirection), (tick, texture) -> skinmaticsLayerState.getState(s).put(tick, texture));
                    }
                }
            }
        }

        skinmaticsData.rightEye.emissive = enableEmissiveRightEye;
        skinmaticsData.leftEye.emissive = enableEmissiveLeftEye;

        SkinmaticsClient.LOGGER.info("Local Skinmatics updated");
    }

    public static void loadTextures(Animatable animatable, BiConsumer<Integer, Identifier> consumer) {
        if (animatable.isEmpty()) return;

        for (Map.Entry<String, List<Integer>> textureEntry : animatable.textures().entrySet()) {
            Identifier id = SkinmaticsClient.TEXTURE_MANAGER.resolve(textureEntry.getKey()).textureId();

            if (id != null && id != MissingSprite.getMissingSpriteId()) {
                for (Integer i : textureEntry.getValue()) {
                    consumer.accept(i, id);
                }
            }
        }
    }
}
