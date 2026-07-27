package dev.proststuff.skinmatics.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.proststuff.skinmatics.client.gui.screen.ProfileScreen;


public class SkinmaticsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ProfileScreen::new;
    }
}
