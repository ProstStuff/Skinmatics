package dev.proststuff.skinmatics.client;

import com.google.gson.annotations.Expose;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.utility.SkinmaticsJsonUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ColorHelper;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class SkinmaticsConfig {
    @Expose public boolean openSkinmaticsInsteadOfSkinCustomization = false;
    @Expose private String profile = "default";
    @Expose public int playerUpdateRange = 64;

    private boolean showPaperDoll = false;
    private PaperDollAlignment paperDollAlignment = PaperDollAlignment.TOP_RIGHT;

    @Expose private String backgroundColor = "#000000";
    @Expose private String foregroundColor = "#FFFFFF";
    @Expose private String accentColor = "#80C4FF";

    public String getProfile() {
        return profile != null ? profile : "default";
    }

    public int getBackgroundColor() {
        return parseColor(backgroundColor);
    }

    public int getBackgroundColor(float alpha) {
        return ColorHelper.withAlpha(alpha, getBackgroundColor());
    }

    public int getForegroundColor() {
        return parseColor(foregroundColor);
    }

    public int getForegroundColor(float alpha) {
        return ColorHelper.withAlpha(alpha, getForegroundColor());
    }

    public int getAccentColor() {
        return parseColor(accentColor);
    }

    public void setBackgroundColor(int color) {
        backgroundColor = String.format("0x%06X", color);
    }

    public void setForegroundColor(int color) {
        foregroundColor = String.format("0x%06X", color);
    }

    public void setAccentColor(int color) {
        accentColor = String.format("0x%06X", color);
    }

    public enum PaperDollAlignment {
        TOP_LEFT,
        TOP_MIDDLE,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
    }

    public static int parseColor(String hex) {
        hex = hex.replace("#", "").replace("0X", "").replace("0x", "");

        if (hex.length() == 6) {
            hex = "FF" + hex;
        }

        return (int) Long.parseLong(hex, 16);
    }

    public void save() {
        try {
            SkinmaticsJsonUtils.write("config", this);
        } catch (IOException e) {
            SkinmaticsClient.LOGGER.warn("Unable to save mod config.", e);
        }
    }

    public static SkinmaticsConfig load() {
        try {
            return SkinmaticsJsonUtils.read("config", SkinmaticsConfig.class, SkinmaticsConfig::new);
        }  catch (IOException e) {
            SkinmaticsClient.LOGGER.warn("Unable to load mod config.", e);
            return new SkinmaticsConfig();
        }
    }
}
