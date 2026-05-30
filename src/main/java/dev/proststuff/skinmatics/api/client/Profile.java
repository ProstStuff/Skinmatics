package dev.proststuff.skinmatics.api.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.render.SkinmaticsRenderTypes;
import dev.proststuff.skinmatics.api.config.field.AnimatableConfigField;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.render.EyeRenderLayer;
import dev.proststuff.utilitary.api.config.ConfigFile;
import dev.proststuff.utilitary.api.config.field.ConfigGroup;
import dev.proststuff.utilitary.api.config.field.value.BooleanConfigField;
import dev.proststuff.utilitary.api.config.field.value.math.IntegerConfigField;
import dev.proststuff.utilitary.api.config.impl.ConfigSerializable;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Profile extends ConfigFile {
    protected final String profile;
    public final ProfileData data;

    // General
    public final BooleanConfigField enabled = new BooleanConfigField("enabled", true);
    public final IntegerConfigField maxTicks = new IntegerConfigField("maxTicks", 0);
    public final BooleanConfigField strongerEmissiveGlow = new BooleanConfigField("strongerEmissiveGlow", false);
    public final BooleanConfigField slim = new BooleanConfigField("slim", false);
    public final BooleanConfigField useCustomSkin = new BooleanConfigField("useCustomSkin", false);
    public final BooleanConfigField addEmissiveSkin = new BooleanConfigField("addEmissiveSkin", false);

    // Cape & Elytra (or wings)
    public final BooleanConfigField showCape = new BooleanConfigField("showCape", true);
    public final BooleanConfigField useCustomCape = new BooleanConfigField("useCustomCape", false);
    public final BooleanConfigField addEmissiveCape = new BooleanConfigField("addEmissiveCape", false);
    public final BooleanConfigField useCustomWings = new BooleanConfigField("useCustomWings", false);
    public final BooleanConfigField addEmissiveWings = new BooleanConfigField("addEmissiveWings", false);

    // Eyes
    public final BooleanConfigField showEyes = new BooleanConfigField("showEyes", false);
    public final BooleanConfigField addEmissiveEyes = new BooleanConfigField("addEmissiveEyes", false);
    public final BooleanConfigField hideRightEye = new BooleanConfigField("rightEyeHidden", false);
    public final BooleanConfigField hideLeftEye = new BooleanConfigField("leftEyeHidden", false);
    public final IntegerConfigField blinkingChance = new IntegerConfigField("blinkingChance", 48).clamp(0, Integer.MAX_VALUE);
    public final IntegerConfigField holdBlinkingFor = new IntegerConfigField("holdBlinkingFor", 4).clamp(0, Integer.MAX_VALUE);

    // Textures
    public final AnimatableConfigField skin = new AnimatableConfigField("skin");
    public final AnimatableConfigField emissiveSkin = new AnimatableConfigField("emissiveSkin");
    public final AnimatableConfigField cape = new AnimatableConfigField("cape");
    public final AnimatableConfigField emissiveCape = new AnimatableConfigField("emissiveCape");
    public final AnimatableConfigField wings = new AnimatableConfigField("wings");
    public final AnimatableConfigField emissiveWings = new AnimatableConfigField("emissiveWings");

    public final ConfigGroup rightEye = new ConfigGroup("rightEye");
    public final ConfigGroup leftEye = new ConfigGroup("leftEye");
    public final ConfigGroup emissiveRightEye = new ConfigGroup("emissiveRightEye");
    public final ConfigGroup emissiveLeftEye = new ConfigGroup("emissiveLeftEye");

    //public final ConfigGroup custom = new ConfigGroup("custom");

    public Profile(String profile) {
        super(SkinmaticsMod.ofSimple("profiles/" + profile));
        this.profile = profile;
        this.data = new ProfileData(this);

        for (EyeRenderLayer.EyeDirection eyeDirection : EyeRenderLayer.EyeDirection.values()) {
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
                useCustomWings,
                addEmissiveWings,
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
                wings,
                emissiveWings,
                rightEye,
                leftEye,
                emissiveRightEye,
                emissiveLeftEye
        );
    }

    public String getProfileName() {
        return profile;
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return strongerEmissiveGlow.get() ? SkinmaticsRenderTypes.eyesNoCull(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }

    @Override
    public void deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
        super.deserialize(jsonElement, context);

        data.clear();

        iterate(skin, data.skin.getState(0));
        iterate(emissiveSkin, data.skin.getState(1));
        iterate(cape, data.cape.getState(0));
        iterate(emissiveCape, data.cape.getState(1));
        iterate(wings, data.wings.getState(0));
        iterate(emissiveWings, data.wings.getState(1));

        iterateEyes(EyeRenderLayer.EyePosition.RIGHT, data, rightEye, false);
        iterateEyes(EyeRenderLayer.EyePosition.LEFT, data, leftEye, false);
        iterateEyes(EyeRenderLayer.EyePosition.RIGHT, data, emissiveRightEye, true);
        iterateEyes(EyeRenderLayer.EyePosition.LEFT, data, emissiveLeftEye, true);
    }

    @Override
    public int hashCode() {
        return profile.hashCode();
    }

    private static void iterateEyes(EyeRenderLayer.EyePosition eyePosition, ProfileData data, ConfigGroup configGroup, boolean emissive) {
        for (Map.Entry<String, ConfigSerializable> entry : configGroup.children.entrySet()) {
            if (entry.getValue() instanceof AnimatableConfigField animatableConfigField) {
                EyeRenderLayer.EyeDirection direction = EyeRenderLayer.EyeDirection.fromSerializedName(entry.getKey());
                iterate(animatableConfigField, data.getEyeLayerState(eyePosition).getState(direction.ordinal() + ProfileData.getEyeLayerStateEmissive(emissive)));
            }
        }
    }

    private static void iterate(AnimatableConfigField configField, TreeMap<Integer, Identifier> storage) {
        for (Map.Entry<String, List<Integer>> entry : configField.get().textures().entrySet()) {
            Identifier id = SkinmaticsTextureManager.INSTANCE.resolve(entry.getKey()).textureIdentifier().identifier();

            if (id != null && id != MissingTextureAtlasSprite.getLocation()) {
                for (Integer i : entry.getValue()) {
                    storage.put(i, id);
                }
            }
        }
    }
}
