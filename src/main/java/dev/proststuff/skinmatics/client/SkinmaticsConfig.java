package dev.proststuff.skinmatics.client;

import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.utilitary.api.config.ConfigFile;
import dev.proststuff.utilitary.api.config.codec.ConfigCodecs;
import dev.proststuff.utilitary.api.config.field.value.BooleanConfigField;
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

    public final BooleanConfigField showPaperDoll = new BooleanConfigField("showPaperDoll", true);
    public final BooleanConfigField alwaysShowPaperDoll = new BooleanConfigField("alwaysShowPaperDoll", false);
    public final IntegerConfigField paperDollShowFor = new IntegerConfigField("paperDollShowFor", 20).clamp(0, Integer.MAX_VALUE);
    public final IntegerConfigField paperDollScale = new IntegerConfigField("paperDollScale", 24).clamp(1, Integer.MAX_VALUE);
    public final IntegerConfigField paperDollYawDeviation = new IntegerConfigField("paperDollYawDeviation", 5).clamp(0, 180);
    public final IntegerConfigField paperDollYawOffset = new IntegerConfigField("paperDollYawOffset", 0).clamp(-180, 180);
    public final BooleanConfigField paperDollMirrorHorizontally = new BooleanConfigField("paperDollMirrorHorizontally", true);
    public final IntegerConfigField horizontalPaperDollOffset = new IntegerConfigField("horizontalPaperDollOffset", 0);
    public final BooleanConfigField paperDollMirrorVertically = new BooleanConfigField("paperDollMirrorVertically", false);
    public final IntegerConfigField verticalPaperDollOffset = new IntegerConfigField("verticalPaperDollOffset", 0);

    public final ColorConfigField backgroundColor = new ColorConfigField("backgroundColor", 0xFF000000);
    public final ColorConfigField foregroundColor = new ColorConfigField("foregroundColor", 0xFFFFFFFF);
    public final ColorConfigField accentColor = new ColorConfigField("accentColor", 0xE88A36);
    public final MapConfigField<String> autoAssign = new MapConfigField<>("autoAssign", new LinkedHashMap<>(), ConfigCodecs.STRING);
    public final IntegerConfigField gridSize = new IntegerConfigField("gridSize", 8).clamp(1, Integer.MAX_VALUE);
    public final ColorConfigField primaryGridColor = new ColorConfigField("primaryGridColor", 0xFF323232);
    public final ColorConfigField secondaryGridColor = new ColorConfigField("secondaryGridColor", 0xFF161616);

    public SkinmaticsConfig() {
        super(SkinmaticsMod.of("config"));
        add(
                profile,
                updateRange,
                showPaperDoll,
                alwaysShowPaperDoll,
                paperDollShowFor,
                paperDollScale,
                paperDollYawDeviation,
                paperDollYawOffset,
                paperDollMirrorHorizontally,
                horizontalPaperDollOffset,
                paperDollMirrorVertically,
                verticalPaperDollOffset,
                backgroundColor,
                foregroundColor,
                accentColor,
                gridSize,
                primaryGridColor,
                secondaryGridColor,
                autoAssign
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
