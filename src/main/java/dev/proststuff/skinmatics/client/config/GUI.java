package dev.proststuff.skinmatics.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.utilitary.api.v1.util.ColorRGB;

public final class GUI {
    static final GUI INSTANCE = new GUI();
    static final Codec<GUI> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    ColorRGB.CODEC.fieldOf("background").forGetter(GUI::getBackgroundColor),
                    ColorRGB.CODEC.fieldOf("foreground").forGetter(GUI::getForegroundColor),
                    ColorRGB.CODEC.fieldOf("accent").forGetter(GUI::getAccentColor),
                    Codec.INT.fieldOf("horizontalProfiles").forGetter(GUI::getHorizontalProfiles),
                    Codec.INT.fieldOf("verticalProfiles").forGetter(GUI::getVerticalProfiles)
            ).apply(inst, GUI::apply)
    );

    private ColorRGB backgroundColor = new ColorRGB(0x000000);
    private ColorRGB foregroundColor = new ColorRGB(0xffffff);
    private ColorRGB accentColor = new ColorRGB(0xE88A36);
    private int horizontalProfiles = 5;
    private int verticalProfiles = 3;

    private GUI() {}

    public void setBackgroundColor(int color) {
        this.backgroundColor.set(color);
    }

    public void setForegroundColor(int color) {
        this.foregroundColor.set(color);
    }

    public void setAccentColor(int color) {
        this.accentColor.set(color);
    }

    public void setProfileFlex(int horizontal, int vertical) {
        this.horizontalProfiles = horizontal;
        this.verticalProfiles = vertical;
    }

    public ColorRGB getBackgroundColor() {
        return backgroundColor;
    }

    public ColorRGB getForegroundColor() {
        return foregroundColor;
    }

    public ColorRGB getAccentColor() {
        return accentColor;
    }

    public int getBackground(float alpha) {
        return getBackgroundColor().get(alpha);
    }

    public int getBackground() {
        return getBackground(0.5F);
    }

    public int getForeground(float alpha) {
        return getForegroundColor().get(alpha);
    }

    public int getForeground() {
        return getForeground(1.0F);
    }

    public int getAccent(float alpha) {
        return getAccentColor().get(alpha);
    }

    public int getAccent() {
        return getAccent(1.0F);
    }

    public int getHorizontalProfiles() {
        return Math.max(horizontalProfiles, 0);
    }

    public int getVerticalProfiles() {
        return Math.max(verticalProfiles, 0);
    }

    private static GUI apply(ColorRGB backgroundColor, ColorRGB foregroundColor, ColorRGB accentColor, int horizontalProfiles, int verticalProfiles) {
        INSTANCE.backgroundColor = backgroundColor;
        INSTANCE.foregroundColor = foregroundColor;
        INSTANCE.accentColor = accentColor;
        INSTANCE.horizontalProfiles = horizontalProfiles;
        INSTANCE.verticalProfiles = verticalProfiles;
        return INSTANCE;
    }
}