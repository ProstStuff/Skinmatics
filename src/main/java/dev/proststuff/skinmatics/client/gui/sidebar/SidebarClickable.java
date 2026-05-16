package dev.proststuff.skinmatics.client.gui.sidebar;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.Clickable;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import dev.proststuff.skinmatics.client.gui.panel.Panel;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class SidebarClickable extends Clickable {
    protected final Identifier texture;
    protected final Panel panel;

    public SidebarClickable(Identifier identifier, Identifier texture, PanelCreator panel) {
        super(identifier, ButtonType.SIDEBAR);
        this.texture = texture;
        this.panel = panel.create(this);
    }

    public Panel getPanel() {
        return panel;
    }

    public Identifier getTexture() {
        return texture;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        if (isFocused() || isHovered()) {
            graphics.fill(0, elementY, CustomizationScreen.SIDEBAR_SIZE, elementY + CustomizationScreen.SIDEBAR_SIZE, isFocused() ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor(0.5F));
        }

        GraphicsUtils.sprite(graphics, getTexture(), elementX, elementY, 16, 16);

        if (isFocused()) {
            getPanel().render(graphics, mouseX, mouseY, CustomizationScreen.SIDEBAR_SIZE, 0);
        }
    }

    @Override
    public int getHeight() {
        return CustomizationScreen.SIDEBAR_SIZE;
    }

    public interface PanelCreator {
        Panel create(SidebarClickable sidebarClickable);
    }
}
