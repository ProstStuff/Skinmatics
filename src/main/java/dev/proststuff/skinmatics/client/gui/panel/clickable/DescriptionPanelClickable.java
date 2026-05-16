package dev.proststuff.skinmatics.client.gui.panel.clickable;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public class DescriptionPanelClickable extends PanelClickable<String> {
    protected final String url;
    protected final OpenPath openFilePath;
    protected final boolean asFile;
    protected final OnPress onPress;

    public DescriptionPanelClickable(Identifier identifier, String url) {
        super(identifier);
        this.url = url;
        this.openFilePath = null;
        this.asFile = false;
        this.onPress = null;
    }

    public DescriptionPanelClickable(Identifier identifier, OpenPath filePath, boolean asFile) {
        super(identifier);
        this.openFilePath = filePath;
        this.asFile = asFile;
        this.url = null;
        this.onPress = null;
    }

    public DescriptionPanelClickable(Identifier identifier, OnPress onPress) {
        super(identifier);
        this.onPress = onPress;
        this.openFilePath = null;
        this.asFile = false;
        this.url = null;
    }

    public DescriptionPanelClickable(Identifier identifier) {
        super(identifier);
        this.url = null;
        this.openFilePath = null;
        this.asFile = false;
        this.onPress = null;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (inside(event.x(), event.y())) {
            if (onPress != null) {
                onPress.onPress(this);
            } else if (url != null && !url.isBlank()) {
                Util.getPlatform().openUri(url);
            } else if (openFilePath != null) {
                if (asFile) {
                    Util.getPlatform().openFile(openFilePath.get().toFile());
                } else {
                    Util.getPlatform().openPath(openFilePath.get());
                }
            }
        }

        return false;
    }


    public interface OpenPath {
        Path get();
    }

    public interface OnPress {
        void onPress(DescriptionPanelClickable panelClickable);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        super.render(graphics, mouseX, mouseY, elementX, elementY);
        int size = CustomizationScreen.PANEL_SIZE / 8;
        int y = elementY + getHeight();
        graphics.fill(elementX + size, y, elementX + CustomizationScreen.PANEL_SIZE - size, y + 1, SkinmaticsConfig.foregroundColor(0.125F));
    }
}
