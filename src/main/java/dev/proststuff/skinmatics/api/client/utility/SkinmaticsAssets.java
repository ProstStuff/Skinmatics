package dev.proststuff.skinmatics.api.client.utility;

import dev.proststuff.skinmatics.Skinmatics;
import net.minecraft.resources.Identifier;

public final class SkinmaticsAssets {
    public static final Identifier
            SKINMATICS = sidebar("skinmatics"),
            CUSTOMIZE = sidebar("customize"),
            SKIN = sidebar("skin"),
            EYES = sidebar("eyes"),
            CAPE = sidebar("cape"),
            WINGS = sidebar("wings"),
            PROFILES = sidebar("profiles"),
            TEXTURES = sidebar("textures"),
            DIRECTORY = sidebar("directory"),

            SAVE = gui("save"),
            DELETE = gui("delete"),
            RELOAD = gui("reload"),
            ERROR = gui("error"),
            NO = gui("no"),
            YES =  gui("yes"),
            XD = gui("xd");

    private static Identifier gui(String name) {
        return Skinmatics.of("textures/gui/" + name + ".png");
    }

    private static Identifier sidebar(String name) {
        return gui("sidebar/" + name);
    }
}
