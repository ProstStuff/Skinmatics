package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.SkinmaticsMod;
import net.minecraft.resources.Identifier;

public interface SkinmaticsGuiAssets {
    Identifier SKINMATICS = SkinmaticsMod.of("textures/icon.png");
    Identifier SKINMATICS_NO_BACKGROUND = gui("skinmatics");

    Identifier AVATARS = gui("avatars");
    Identifier CUSTOMIZE = gui("customize");
    Identifier DELETE = gui("delete");
    Identifier NEW_AVATAR = gui("new_avatar");
    Identifier ERROR = gui("error");

    Identifier PAPER_DOLL = gui("paper_doll");
    Identifier TEXTURES = gui("textures");
    Identifier FOLDER = gui("folder");
    Identifier BUTTON_FALSE = gui("button_false");
    Identifier BUTTON_TRUE = gui("button_true");
    Identifier GRID = gui("grid");
    Identifier LIST = gui("list");

    private static Identifier gui(String name) {
        return SkinmaticsMod.of("textures/gui/" + name + ".png");
    }

    private static Identifier entity(String name) {
        return SkinmaticsMod.of("textures/entity/" + name + ".png");
    }
}