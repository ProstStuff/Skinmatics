package dev.proststuff.skinmatics.client.skinmatics.profile;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.eyes.EyeGroups;
import dev.proststuff.skinmatics.client.skinmatics.profile.data.*;
import dev.proststuff.skinmatics.client.skinmatics.texture.Animatable;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
import dev.proststuff.utilitary.config.v1.ConfigManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProfilePresets {
    public static final int DEFAULT_BLINK_CHANCE = 40;
    public static final int DEFAULT_BLINK_TICKS = 5;

    public static final Profile STEVE;
    public static final Profile ALEX;

    static {
        Animatable steveSkinTextures = Animatable.empty();
        steveSkinTextures.set(TextureData.reference("minecraft:textures/entity/player/wide/steve.png"), 0);

        Map<String, Eyes.EyeTextureSet> steveEyes = new LinkedHashMap<>();
        Map<String, Animatable> steveRightEye = new LinkedHashMap<>();
        Map<String, Animatable> steveLeftEye = new LinkedHashMap<>();

        steveEyes.put(EyeGroups.RIGHT, new Eyes.EyeTextureSet(steveRightEye));
        steveEyes.put(EyeGroups.LEFT, new Eyes.EyeTextureSet(steveLeftEye));

        STEVE = new Profile(
                "Steve",
                0,
                false,
                new Skin(false, true, false, steveSkinTextures, Animatable.EMPTY),
                new Cape(true, false, false, Animatable.EMPTY, Animatable.EMPTY),
                new Wings(false, false, Animatable.EMPTY, Animatable.EMPTY),
                new Eyes(false, List.of(), List.of(), DEFAULT_BLINK_CHANCE, DEFAULT_BLINK_TICKS, steveEyes, new HashMap<>()),
                new Lights()
        );

        Animatable alexSkinTextures = Animatable.empty();
        alexSkinTextures.set(TextureData.reference("minecraft:textures/entity/player/slim/alex.png"), 0);
        Map<String, Eyes.EyeTextureSet> alexEyes = new LinkedHashMap<>();
        Map<String, Animatable> alexRightEye = new LinkedHashMap<>();
        Map<String, Animatable> alexLeftEye = new LinkedHashMap<>();

        alexEyes.put(EyeGroups.RIGHT, new Eyes.EyeTextureSet(alexRightEye));
        alexEyes.put(EyeGroups.LEFT, new Eyes.EyeTextureSet(alexLeftEye));

        ALEX = new Profile(
                "Alex",
                0,
                false,
                new Skin(true, true, false, alexSkinTextures, Animatable.EMPTY),
                new Cape(true, false, false, Animatable.EMPTY, Animatable.EMPTY),
                new Wings(false, false, Animatable.EMPTY, Animatable.EMPTY),
                new Eyes(false, List.of(), List.of(), DEFAULT_BLINK_CHANCE, DEFAULT_BLINK_TICKS, alexEyes, new HashMap<>()),
                new Lights()
        );
    }

    public static void savePresets() {
        ConfigManager.save(SkinmaticsMod.ofSimple("profiles/presets/steve"), Profile.TYPE, STEVE);
        ConfigManager.save(SkinmaticsMod.ofSimple("profiles/presets/alex"), Profile.TYPE, ALEX);
    }
}
