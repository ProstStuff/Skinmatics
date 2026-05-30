package dev.proststuff.skinmatics.api.client.gui.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SidebarButton extends AbstractSkinmaticsRenderable implements ClickableRenderable {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    public final boolean bottom;
    protected final Identifier icon;
    protected final AbstractPanel panel;
    protected final Supplier<Path> pathSupplier;

    public SidebarButton(SimpleIdentifier identifier, Identifier icon, boolean bottom, AbstractPanel panel) {
        super(identifier);
        this.bottom = bottom;
        this.icon = icon;
        this.panel = panel;
        this.pathSupplier = null;
    }

    public SidebarButton(SimpleIdentifier identifier, Identifier icon, boolean bottom, Supplier<Path> pathSupplier) {
        super(identifier);
        this.bottom = bottom;
        this.icon = icon;
        this.pathSupplier = pathSupplier;
        this.panel = null;
    }

    public SidebarButton(SimpleIdentifier identifier, Identifier icon, boolean bottom) {
        super(identifier);
        this.bottom = bottom;
        this.icon = icon;
        this.pathSupplier = null;
        this.panel = null;
    }

    public AbstractPanel getPanel() {
        return panel;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        if (panel != null) {
            panel.init(getScreenWidth(), getScreenHeight());
        }
    }

    @Override
    public void unfocus() {
        ClickableRenderable.super.unfocus();

        if (panel != null) {
            if (AbstractPanel.TOP_PANEL == panel) {
                AbstractPanel.TOP_PANEL = null;
            }

            if (AbstractPanel.BOTTOM_PANEL == panel) {
                AbstractPanel.BOTTOM_PANEL = null;
            }
        }
    }

    @Override
    public void focus() {
        if (pathSupplier != null) {
            Path path = pathSupplier.get();
            File file = path.toFile();

            if (file.isFile()) {
                Util.getPlatform().openFile(file);
            } else {
                Util.getPlatform().openPath(path);
            }
        }

        if (panel != null) {
            AbstractPanel.TOP_PANEL = bottom ? AbstractPanel.TOP_PANEL : panel;
            AbstractPanel.BOTTOM_PANEL = bottom ? panel : AbstractPanel.BOTTOM_PANEL;

            panel.setSource(this);
            ClickableRenderable.super.focus();
        }
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        return button == 0 && in(mouseX, mouseY);
    }

    @Override
    public String getCategory() {
        return "sidebar" + (bottom ? "_bottom" : "");
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderClickableState(graphics);
        GraphicsUtils.sprite(graphics, icon, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean isPressing(int button, double mouseX, double mouseY) {
        return button == 0 && !isFocused() && ClickableRenderable.super.isPressing(button, mouseX, mouseY);
    }
}