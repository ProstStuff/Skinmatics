package dev.proststuff.skinmatics.api.skinmatics;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.utilitary.api.config.ConfigFile;
import dev.proststuff.utilitary.api.config.field.value.StringConfigField;
import dev.proststuff.utilitary.api.config.field.value.math.ColorConfigField;
import dev.proststuff.utilitary.api.config.field.value.math.IntegerConfigField;

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

    public static int backgroundColor() {
        return INSTANCE.backgroundColor.get(0.5F);
    }

    public static int backgroundColor(float alpha) {
        return INSTANCE.backgroundColor.get(alpha);
    }

    public static int foregroundColor() {
        return INSTANCE.foregroundColor.get(1.0F);
    }

    public static int foregroundColor(float alpha) {
        return INSTANCE.foregroundColor.get(alpha);
    }

    public static int accentColor() {
        return INSTANCE.accentColor.get(1.0F);
    }

    public static int accentColor(float alpha) {
        return INSTANCE.accentColor.get(alpha);
    }
}
