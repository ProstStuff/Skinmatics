package dev.proststuff.skinmatics.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.proststuff.skinmatics.SkinmaticsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


public class SkinmaticsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SkinmaticsClient::openCustomizationScreen;
    }
}
