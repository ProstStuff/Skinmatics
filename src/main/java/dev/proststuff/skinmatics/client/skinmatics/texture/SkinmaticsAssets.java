package dev.proststuff.skinmatics.client.skinmatics.texture;

import dev.proststuff.skinmatics.SkinmaticsMod;
import net.minecraft.resources.Identifier;

public final class SkinmaticsAssets {
    public static final Identifier SKINMATICS = SkinmaticsMod.of("textures/icon.png");

    public static final Identifier SKINMATICS_NO_BACKGROUND = gui("skinmatics");
    public static final Identifier CUSTOMIZE = gui("customize");

    public static final Identifier PROFILES = gui("profiles");
    public static final Identifier DELETE = gui("delete");
    public static final Identifier ERROR = gui("error");

    public static final Identifier BUTTON_FALSE = gui("button_false");
    public static final Identifier BUTTON_TRUE = gui("button_true");

    public static final Identifier PAPER_DOLL = gui("paper_doll");

    public static final Identifier TEXTURES = gui("textures");
    public static final Identifier NEW_PROFILE = gui("new_profile");

    private static Identifier gui(String name) {
        return SkinmaticsMod.of("textures/gui/" + name + ".png");
    }
}