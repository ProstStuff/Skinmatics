package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.utilitary.api.v1.util.ActionHistory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class CustomizationScreen extends AbstractSkinmaticsScreen {
    public static final Component TITLE = Component.translatable("skinmatics.customization");

    private final ActionHistory actionHistory = new ActionHistory();
    private final Profile profile;
    private boolean unsaved = false;

    public CustomizationScreen(Screen parent, Profile profile) {
        super(TITLE, parent);
        this.profile = profile;
    }

    @Override
    public void init() {

    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(0, 0, 16, height, backgroundColor());
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void onClose() {
        if (unsaved) return;
        super.onClose();
    }
}