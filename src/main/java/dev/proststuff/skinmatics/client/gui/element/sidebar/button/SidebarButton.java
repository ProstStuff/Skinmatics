package dev.proststuff.skinmatics.client.gui.element.sidebar.button;

import dev.proststuff.skinmatics.client.gui.element.SimpleButton;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Supplier;

public class SidebarButton extends SimpleButton {
    protected static @Nullable SidebarButton focused = null;

    protected final boolean bottom;
    protected final @Nullable Panel panel;
    protected final @Nullable Supplier<Path> path;
    protected final @Nullable OnClick onClick;

    public SidebarButton(SimpleIdentifier identifier, Identifier texture, @Nullable Panel panel, boolean bottom) {
        super(identifier, texture);
        this.panel = panel;
        this.path = null;
        this.onClick = null;
        this.bottom = bottom;

        if (panel != null) {
            panel.setSidebar(this);
        }
    }

    public SidebarButton(SimpleIdentifier identifier, Identifier texture, @Nullable Supplier<Path> path, boolean bottom) {
        super(identifier, texture);
        this.panel = null;
        this.path = path;
        this.onClick = null;
        this.bottom = bottom;
    }

    public SidebarButton(SimpleIdentifier identifier, Identifier texture, @Nullable OnClick onClick, boolean bottom) {
        super(identifier, texture);
        this.panel = null;
        this.path = null;
        this.onClick = onClick;
        this.bottom = bottom;
    }

    public SidebarButton(SimpleIdentifier identifier, Identifier texture, boolean bottom) {
        super(identifier, texture);
        this.panel = null;
        this.path = null;
        this.onClick = null;
        this.bottom = bottom;
    }

    public boolean isFocused() {
        return focused == this;
    }

    public void focus() {
        focused = this;
    }

    public @Nullable Supplier<Path> getPath() {
        return path;
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

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return event.button() == 0 && inside(event.x(), event.y()) && (onClick == null || onClick.onClick(this));
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

    public static void reset() {
        unfocus();
        unhover();
    }

    public interface OnClick {
        boolean onClick(SidebarButton button);
    }
}
