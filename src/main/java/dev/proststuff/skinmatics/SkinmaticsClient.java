package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.SidebarButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.skinmatics.client.SkinmaticsClientEvents;
import dev.proststuff.skinmatics.client.gui.PaperDollRenderer;
import dev.proststuff.skinmatics.client.gui.customization.SkinmaticsCustomizationScreen;
import dev.proststuff.skinmatics.client.gui.customization.panel.CustomizePanel;
import dev.proststuff.skinmatics.client.gui.customization.panel.HomePanel;
import dev.proststuff.skinmatics.client.gui.customization.panel.ProfilesPanel;
import dev.proststuff.skinmatics.client.gui.customization.panel.TexturesPanel;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.Screen;

public class SkinmaticsClient implements ClientModInitializer, SkinmaticsMod {
    @Override
    public void onInitializeClient() {
        SkinmaticsClientEvents.init();

        SkinmaticsCustomizationScreen.supplySidebarButton(
                () -> new SidebarButton(SkinmaticsMod.ofSimple("home"), SkinmaticsAssets.SKINMATICS, false, new HomePanel()),
                () -> new SidebarButton(SkinmaticsMod.ofSimple("profiles"), SkinmaticsAssets.PROFILES, false, new ProfilesPanel()),
                () -> new SidebarButton(SkinmaticsMod.ofSimple("customize"), SkinmaticsAssets.CUSTOMIZE, false, new CustomizePanel()),
                () -> new SidebarButton(SkinmaticsMod.ofSimple("timeline"), SkinmaticsAssets.TIMELINE, true),
                () -> new SidebarButton(SkinmaticsMod.ofSimple("textures"), SkinmaticsAssets.TEXTURES, true, new TexturesPanel()),
                () -> new SidebarButton(SkinmaticsMod.ofSimple("directory"), SkinmaticsAssets.DIRECTORY, true, () -> FileJsonUtils.getConfigPath().resolve(SkinmaticsMod.ID))
        );

        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, PaperDollRenderer.IDENTIFIER, PaperDollRenderer.INSTANCE);
    }

    public static Screen openCustomizationScreen(Screen parent) {
        return new SkinmaticsCustomizationScreen(parent);
    }
}
