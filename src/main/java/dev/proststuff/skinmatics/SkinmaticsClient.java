package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.client.SkinmaticsClientEvents;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SkinmaticsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SkinmaticsClient");
    public static final SkinmaticsConfig CONFIG = SkinmaticsConfig.INSTANCE;
    public static SkinmaticsTextureManager TEXTURE_MANAGER;

    @Override
    public void onInitializeClient() {
        SkinmaticsClientEvents.init();
    }

    public static Screen openCustomizationScreen(Screen parent) {
        return new CustomizationScreen(parent);
    }

    public static UUID getProfileUUID() {
        return Minecraft.getInstance().getGameProfile().id();
    }
}
