package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.SidebarButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.skinmatics.client.SkinmaticsClientEvents;
import dev.proststuff.skinmatics.client.SkinmaticsClientRegistration;
import dev.proststuff.skinmatics.client.gui.PaperDollRenderer;
import dev.proststuff.skinmatics.client.gui.customization.SkinmaticsCustomizationScreen;
import dev.proststuff.skinmatics.client.gui.customization.panel.*;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.Screen;

public class SkinmaticsClient implements ClientModInitializer, SkinmaticsMod {
    @Override
    public void onInitializeClient() {
        SkinmaticsClientEvents.init();

        SkinmaticsClientRegistration registration = SkinmaticsClientRegistration.INSTANCE;
        registration.addSidebarButton(SkinmaticsMod.ofSimple("home"), (id) -> new SidebarButton(id, SkinmaticsAssets.SKINMATICS, false, new HomePanel()));
        registration.addSidebarButton(SkinmaticsMod.ofSimple("profiles"), (id) -> new SidebarButton(id, SkinmaticsAssets.PROFILES, false, new ProfilesPanel()));
        registration.addSidebarButton(SkinmaticsMod.ofSimple("customize"), (id) -> new SidebarButton(id, SkinmaticsAssets.CUSTOMIZE, false, new CustomizePanel()));
        registration.addSidebarButton(SkinmaticsMod.ofSimple("timeline"), (id) -> new SidebarButton(id, SkinmaticsAssets.TIMELINE, false, new TimelinePanel()));
        registration.addSidebarButton(SkinmaticsMod.ofSimple("textures"), (id) -> new SidebarButton(id, SkinmaticsAssets.TEXTURES, true, new TexturesPanel()));
        registration.addSidebarButton(SkinmaticsMod.ofSimple("directory"), (id) -> new SidebarButton(id, SkinmaticsAssets.DIRECTORY, true, () -> FileJsonUtils.getConfigPath().resolve(SkinmaticsMod.ID)));

        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, PaperDollRenderer.IDENTIFIER, PaperDollRenderer.INSTANCE);
    }

    public static Screen openCustomizationScreen(Screen parent) {
        return new SkinmaticsCustomizationScreen(parent);
    }
}
