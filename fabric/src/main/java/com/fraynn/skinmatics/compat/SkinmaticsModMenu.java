package com.fraynn.skinmatics.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.fraynn.skinmatics.gui.screen.SkinmaticsGui;


public class SkinmaticsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SkinmaticsGui::openScreen;
    }
}
