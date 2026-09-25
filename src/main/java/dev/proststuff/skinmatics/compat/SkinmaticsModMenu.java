package dev.proststuff.skinmatics.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.proststuff.skinmatics.gui.SkinmaticsGui;


public class SkinmaticsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SkinmaticsGui::openScreen;
    }
}
