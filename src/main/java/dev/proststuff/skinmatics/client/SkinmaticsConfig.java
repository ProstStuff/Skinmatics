package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.utilitary.api.config.ConfigFile;
import dev.proststuff.utilitary.api.config.codec.ConfigCodecs;
import dev.proststuff.utilitary.api.config.field.value.MapConfigField;
import dev.proststuff.utilitary.api.config.field.value.StringConfigField;
import dev.proststuff.utilitary.api.config.field.value.math.IntegerConfigField;
import dev.proststuff.utilitary.api.config.field.value.utilitary.ColorConfigField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.LinkedHashMap;

@Environment(EnvType.CLIENT)
public class SkinmaticsConfig extends ConfigFile {
    public static final SkinmaticsConfig INSTANCE = new SkinmaticsConfig();

    public final StringConfigField profile = new StringConfigField("profile", "default");
    public final IntegerConfigField updateRange = new IntegerConfigField("updateRange", 64).clamp(1, Integer.MAX_VALUE);
    public final ColorConfigField backgroundColor = new ColorConfigField("backgroundColor", 0xFF000000);
    public final ColorConfigField foregroundColor = new ColorConfigField("foregroundColor", 0xFFFFFFFF);
    public final ColorConfigField accentColor = new ColorConfigField("accentColor", 0xE88A36);
    public final MapConfigField<String> autoAssign = new MapConfigField<>("autoAssign", new LinkedHashMap<>(), ConfigCodecs.STRING);
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
                accentColor,
                autoAssign
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
