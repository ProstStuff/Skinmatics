package dev.proststuff.skinmatics.client.skinmatics;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.config.field.AnimatableConfigField;
import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import dev.proststuff.utilitary.serialization.ConfigFile;
import dev.proststuff.utilitary.serialization.content.ConfigGroup;
import dev.proststuff.utilitary.serialization.content.field.BooleanConfigField;
import dev.proststuff.utilitary.serialization.content.field.math.IntegerConfigField;
import dev.proststuff.utilitary.serialization.impl.ConfigSerializable;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.*;

public class Profile extends ConfigFile {
    protected final String profile;
    public final ProfileData data;

    public final BooleanConfigField enabled = new BooleanConfigField("enabled", true);
    public final IntegerConfigField maxTicks = new IntegerConfigField("maxTicks", 0);
    public final BooleanConfigField strongerEmissiveGlow = new BooleanConfigField("strongerEmissiveGlow", false);
    public final BooleanConfigField slim = new BooleanConfigField("slim", false);
    public final BooleanConfigField useCustomSkin = new BooleanConfigField("useCustomSkin", false);
    public final BooleanConfigField addEmissiveSkin = new BooleanConfigField("addEmissiveSkin", false);
    public final BooleanConfigField showCape = new BooleanConfigField("showCape", true);
    public final BooleanConfigField useCustomCape = new BooleanConfigField("useCustomCape", false);
    public final BooleanConfigField addEmissiveCape = new BooleanConfigField("addEmissiveCape", false);
    public final BooleanConfigField useCustomElytra = new BooleanConfigField("useCustomElytra", false);
    public final BooleanConfigField addEmissiveElytra = new BooleanConfigField("addEmissiveElytra", false);

    public final BooleanConfigField showEyes = new BooleanConfigField("showEyes", false);
    public final BooleanConfigField addEmissiveEyes = new BooleanConfigField("addEmissiveEyes", false);
    public final BooleanConfigField hideRightEye = new BooleanConfigField("rightEyeHidden", false);
    public final BooleanConfigField hideLeftEye = new BooleanConfigField("leftEyeHidden", false);
    public final IntegerConfigField blinkingChance = new IntegerConfigField("blinkingChance", 48).clamp(0, Integer.MAX_VALUE);
    public final IntegerConfigField holdBlinkingFor = new IntegerConfigField("holdBlinkingFor", 4).clamp(0, Integer.MAX_VALUE);

    public final AnimatableConfigField skin = new AnimatableConfigField("skin");
    public final AnimatableConfigField emissiveSkin = new AnimatableConfigField("emissiveSkin");
    public final AnimatableConfigField cape = new AnimatableConfigField("cape");
    public final AnimatableConfigField emissiveCape = new AnimatableConfigField("emissiveCape");
    public final AnimatableConfigField elytra = new AnimatableConfigField("elytra");
    public final AnimatableConfigField emissiveElytra = new AnimatableConfigField("emissiveElytra");

    public final ConfigGroup rightEye = new ConfigGroup("rightEye");
    public final ConfigGroup leftEye = new ConfigGroup("leftEye");
    public final ConfigGroup emissiveRightEye = new ConfigGroup("emissiveRightEye");
    public final ConfigGroup emissiveLeftEye = new ConfigGroup("emissiveLeftEye");

    public Profile(String profile) {
        super(Skinmatics.of("profiles/" + profile));
        this.profile = profile;
        this.data = new ProfileData(this);

        for (EyeFeatureRenderer.EyeDirection eyeDirection : EyeFeatureRenderer.EyeDirection.values()) {
            String name = eyeDirection.getSerializedName();
            rightEye.add(new AnimatableConfigField(name));
            leftEye.add(new AnimatableConfigField(name));
            emissiveRightEye.add(new AnimatableConfigField(name));
            emissiveLeftEye.add(new AnimatableConfigField(name));
        }

        add(
                enabled,
                maxTicks,
                strongerEmissiveGlow,
                slim,
                useCustomSkin,
                addEmissiveSkin,
                showCape,
                useCustomCape,
                addEmissiveCape,
                useCustomElytra,
                addEmissiveElytra,
                showEyes,
                addEmissiveEyes,
                hideRightEye,
                hideLeftEye,
                blinkingChance,
                holdBlinkingFor,
                skin,
                emissiveSkin,
                cape,
                emissiveCape,
                elytra,
                emissiveElytra,
                rightEye,
                leftEye,
                emissiveRightEye,
                emissiveLeftEye
        );
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return strongerEmissiveGlow.get() ? SkinmaticsRenderTypes.eyesNoCull(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }

    @Override
    public void deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
        super.deserialize(jsonElement, context);
        Skinmatics.LOGGER.info("Deserialize");

        data.skin.clear();
        data.cape.clear();
        data.elytra.clear();
        data.rightEye.clear();
        data.leftEye.clear();

        iterate(skin, data.skin.getState(0));
        iterate(emissiveSkin, data.skin.getState(1));
        iterate(cape, data.cape.getState(0));
        iterate(emissiveCape, data.cape.getState(1));
        iterate(elytra, data.elytra.getState(0));
        iterate(emissiveElytra, data.elytra.getState(1));

        iterateEyes(EyeFeatureRenderer.EyePosition.RIGHT, data, rightEye, false);
        iterateEyes(EyeFeatureRenderer.EyePosition.LEFT, data, leftEye, false);
        iterateEyes(EyeFeatureRenderer.EyePosition.RIGHT, data, emissiveRightEye, true);
        iterateEyes(EyeFeatureRenderer.EyePosition.LEFT, data, emissiveLeftEye, true);
    }

    @Override
    public int hashCode() {
        return profile.hashCode();
    }

    private static void iterateEyes(EyeFeatureRenderer.EyePosition eyePosition, ProfileData data, ConfigGroup configGroup, boolean emissive) {
        for (Map.Entry<String, ConfigSerializable> entry : configGroup.children.entrySet()) {
            if (entry.getValue() instanceof AnimatableConfigField animatableConfigField) {
                EyeFeatureRenderer.EyeDirection direction = EyeFeatureRenderer.EyeDirection.fromSerializedName(entry.getKey());
                iterate(animatableConfigField, data.getEyeLayerState(eyePosition).getState(direction.ordinal() + ProfileData.getEyeLayerStateEmissive(emissive)));
            }
        }
    }

    private static void iterate(AnimatableConfigField configField, TreeMap<Integer, Identifier> storage) {
        for (Map.Entry<String, List<Integer>> entry : configField.get().textures().entrySet()) {
            Identifier id = SkinmaticsClient.TEXTURE_MANAGER.resolve(entry.getKey()).textureIdentifier().identifier();

            if (id != null && id != MissingTextureAtlasSprite.getLocation()) {
                for (Integer i : entry.getValue()) {
                    storage.put(i, id);
                }
            }
        }
    }
}
