package dev.proststuff.skinmatics.client.gui.panel.clickable;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.Clickable;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import dev.proststuff.skinmatics.client.gui.impl.ScreenTracker;
import dev.proststuff.utilitary.serialization.content.ConfigField;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class PanelClickable<T> extends Clickable implements ScreenTracker, ConfigHolder<T> {
    protected int width = 0;
    protected int height = 0;
    protected int y = 0;
    protected final ConfigField<T> field;

    public PanelClickable(Identifier identifier, ConfigField<T> configField) {
        super(identifier, ButtonType.PANEL);
        this.field = configField;
    }

    public PanelClickable(Identifier identifier) {
        this(identifier, null);
    }

    @Override
    public ConfigField<T> getField() {
        return field;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight() {
        Component component = asComponent();
        Font font = GraphicsUtils.font();
        return font.wordWrapHeight(component, CustomizationScreen.PANEL_SIZE - 4) + font.lineHeight / 2;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        graphics.textWithWordWrap(GraphicsUtils.font(), asComponent(), elementX + 4, elementY + GraphicsUtils.font().lineHeight / 2, CustomizationScreen.PANEL_SIZE - 4, SkinmaticsConfig.foregroundColor());
    }

    public boolean inside(double pointX, double pointY) {
        if (y > height) {
            return false;
        }
        int sidebar = CustomizationScreen.SIDEBAR_SIZE;
        int panel = CustomizationScreen.PANEL_SIZE;

        return pointX >= sidebar && pointX < sidebar + panel && pointY >= y && pointY < y + getHeight();
    }
}
