package dev.proststuff.skinmatics.client.gui.element.sidebar.button;

import dev.proststuff.skinmatics.client.gui.element.SimpleButton;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.api.skinmatics.SkinmaticsConfig;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SidebarButton extends SimpleButton {
    protected static @Nullable SidebarButton focused = null;
    protected static @Nullable SidebarButton hovered = null;

    protected final boolean bottom;
    protected final @Nullable Panel panel;

    public SidebarButton(Identifier identifier, Identifier texture, @Nullable Panel panel, boolean bottom) {
        super(identifier, texture);
        this.panel = panel;
        this.bottom = bottom;

        if (panel != null) {
            panel.setSidebar(this);
        }
    }

    public boolean isHovered() {
        return hovered == this;
    }

    public boolean isFocused() {
        return focused == this;
    }

    public void hover() {
        hovered = this;
    }

    public void focus() {
        focused = this;
    }

    public SidebarButton(Identifier identifier, Identifier texture, boolean bottom) {
        this(identifier, texture, null, bottom);
    }

    public @Nullable Panel getPanel() {
        return panel;
    }

    public boolean isBottom() {
        return bottom;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        boolean focused = isFocused();
        boolean hovered = isHovered();

        if (focused || hovered) {
            graphics.fill(x, y, x + getWidth(), y + getHeight(), focused ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor(0.5F));
        }

        GraphicsUtils.sprite(graphics, texture, x, y, 0.0F, 0.0F, getWidth(), getHeight(), getWidth(), getHeight(), SkinmaticsConfig.foregroundColor());
    }

    public static @Nullable SidebarButton getFocused() {
        return focused;
    }

    public static @Nullable SidebarButton getHovered() {
        return focused;
    }

    public static SidebarButton unfocus() {
        SidebarButton button = focused;
        focused = null;
        return button;
    }

    public static SidebarButton unhover() {
        SidebarButton button = hovered;
        hovered = null;
        return button;
    }

    public static void reset() {
        unfocus();
        unhover();
    }
}
