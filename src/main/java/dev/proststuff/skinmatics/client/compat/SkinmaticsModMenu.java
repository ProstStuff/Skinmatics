package dev.proststuff.skinmatics.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.proststuff.skinmatics.SkinmaticsClient;


public class SkinmaticsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SkinmaticsClient::openCustomizationScreen;
    }
}
