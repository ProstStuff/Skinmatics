package dev.proststuff.skinmatics.client.config;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.utilitary.serialization.ConfigFile;
import dev.proststuff.utilitary.serialization.content.field.StringConfigField;
import dev.proststuff.utilitary.serialization.content.field.math.ColorConfigField;
import dev.proststuff.utilitary.serialization.content.field.math.IntegerConfigField;
import dev.proststuff.utilitary.utility.UtilitaryJsonUtils;

import java.nio.file.Path;

public class SkinmaticsConfig extends ConfigFile {
    public static final SkinmaticsConfig INSTANCE = new SkinmaticsConfig();

    public final StringConfigField profile = new StringConfigField("profile", "default");
    public final IntegerConfigField updateRange = new IntegerConfigField("updateRange", 64).clamp(1, Integer.MAX_VALUE);
    public final ColorConfigField backgroundColor = new ColorConfigField("backgroundColor", 0xFF000000);
    public final ColorConfigField foregroundColor = new ColorConfigField("foregroundColor", 0xFFFFFFFF);
    public final ColorConfigField accentColor = new ColorConfigField("accentColor", 0xFF3948CC);
    //public final IntegerConfigField gridSize = new IntegerConfigField("gridSize", 8).clamp(1, Integer.MAX_VALUE);
    //public final ColorConfigField primaryGridColor = new ColorConfigField("primaryGridColor", 0xFF808080);
    //public final ColorConfigField secondaryGridColor = new ColorConfigField("secondaryGridColor", 0xFFFFFFFF);

    public SkinmaticsConfig() {
        super(Skinmatics.of("config"));
        add(
                profile,
                updateRange,
                backgroundColor,
                foregroundColor,
                accentColor//,
                //gridSize,
                //primaryGridColor,
                //secondaryGridColor
        );
    }

    public Profile loadProfile() {
        String name = profile.get();
        Path destination = UtilitaryJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles").resolve(name);
        return UtilitaryJsonUtils.read(destination, Profile.class, () -> new Profile(name));
    }

    public static int backgroundColor() {
        return INSTANCE.backgroundColor.get().get(0.5F);
    }

    public static int backgroundColor(float alpha) {
        return INSTANCE.backgroundColor.get().get(alpha);
    }

    public static int foregroundColor() {
        return INSTANCE.foregroundColor.get().get(1.0F);
    }

    public static int foregroundColor(float alpha) {
        return INSTANCE.foregroundColor.get().get(alpha);
    }

    public static int accentColor() {
        return INSTANCE.accentColor.get().get(1.0F);
    }

    public static int accentColor(float alpha) {
        return INSTANCE.accentColor.get().get(alpha);
    }
}
