package dev.proststuff.skinmatics.api.client.texture;

import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import net.minecraft.resources.Identifier;

public final class SkinmaticsAssets {
    public static final Identifier SKINMATICS = sidebar("skinmatics");
    public static final Identifier CUSTOMIZE = sidebar("customize");
    public static final Identifier SKIN = sidebar("skin");
    public static final Identifier EYES = sidebar("eyes");
    public static final Identifier CAPE = sidebar("cape");
    public static final Identifier WINGS = sidebar("wings");

    public static final Identifier PROFILES = sidebar("profiles");
    public static final Identifier TEXTURES = sidebar("textures");
    public static final Identifier TIMELINE = sidebar("timeline");
    public static final Identifier DIRECTORY = sidebar("directory");

    public static final Identifier SAVE = gui("save");
    public static final Identifier DELETE = gui("delete");
    public static final Identifier RELOAD = gui("reload");
    public static final Identifier EXPAND = gui("expand");
    public static final Identifier COLLAPSE = gui("collapse");
    public static final Identifier ERROR = gui("error");
    public static final Identifier NO = gui("no");
    public static final Identifier YES =  gui("yes");
    public static final Identifier XD = gui("xd");

    private static Identifier gui(String name) {
        return SkinmaticsMod.of("textures/gui/" + name + ".png");
    }

    private static Identifier sidebar(String name) {
        return gui("sidebar/" + name);
    }
}