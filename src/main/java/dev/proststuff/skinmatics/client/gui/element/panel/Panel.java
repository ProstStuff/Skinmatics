package dev.proststuff.skinmatics.client.gui.element.panel;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.element.panel.button.PanelButton;
import dev.proststuff.skinmatics.client.gui.element.sidebar.Sidebar;
import dev.proststuff.skinmatics.client.gui.element.sidebar.button.SidebarButton;
import dev.proststuff.skinmatics.api.client.impl.gui.KeyEventListener;
import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Panel extends AbstractRenderable implements MouseEventListener, KeyEventListener {
    public static final int WIDTH = 128;

    protected final List<PanelButton> buttons = new ArrayList<>();
    protected double scrollingPosition = 0;
    protected SidebarButton sidebar;

    public Panel(SimpleIdentifier identifier) {
        super(identifier);
    }

    public Panel(SimpleIdentifier identifier, Supplier<MutableComponent> component) {
        super(identifier, component);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        setPosition(Sidebar.SIDEBAR_WIDTH, 0);

        int currentY = 16 - (int) scrollingPosition;

        for (PanelButton button : buttons) {
            button.setPosition(x, currentY);
            button.init(width, height);

            currentY += button.getHeight();
        }
    }

    public <P extends PanelButton> P addButton(P button) {
        buttons.add(button);
        return button;
    }

    public void setSidebar(SidebarButton sidebar) {
        this.sidebar = sidebar;
    }

    public int getHeightContents() {
        int height = sidebar != null ? SidebarButton.SIZE : 0;

        for (PanelButton button : buttons) {
            height += button.getHeight();
        }

        return height;
    }

    public void increment(double deltaY) {
        scrollingPosition += deltaY;

        if (scrollingPosition < 0) {
            scrollingPosition = 0;
        }

        int visibleHeight = getHeight();
        int maxScroll = Math.max(0, getHeightContents() - visibleHeight);

        if (scrollingPosition > maxScroll) {
            scrollingPosition = maxScroll;
        }
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return screenHeight;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.fill(SidebarButton.SIZE, 0, SidebarButton.SIZE + getWidth(), getHeight(), SkinmaticsConfig.backgroundColor());
        renderButtons(graphics, mouseX, mouseY, tickDelta, renderTitleBar(graphics));
    }

    public void renderButtons(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, int startY) {
        int viewportTop = y;
        int viewportBottom = y + getHeight();

        graphics.enableScissor(Sidebar.SIDEBAR_WIDTH, viewportTop + 16, Sidebar.SIDEBAR_WIDTH + getWidth(), viewportBottom);

        int currentY = startY - (int) scrollingPosition;

        for (PanelButton button : buttons) {
            int buttonTop = currentY;
            int buttonBottom = currentY + button.getHeight();
            boolean visible = buttonBottom >= viewportTop && buttonTop <= viewportBottom;

            if (visible) {
                button.setPosition(x, currentY);
                button.render(graphics, mouseX, mouseY, tickDelta);
            }

            currentY += button.getHeight();
        }

        graphics.disableScissor();
    }

    public int renderTitleBar(GuiGraphicsExtractor graphics) {
        if (sidebar == null) return 0;
        graphics.fill(SidebarButton.SIZE, 0, SidebarButton.SIZE + Panel.WIDTH, SidebarButton.SIZE, SkinmaticsConfig.accentColor());
        graphics.verticalLine(SidebarButton.SIZE, -1, SidebarButton.SIZE, SkinmaticsConfig.foregroundColor());
        GraphicsUtils.text(graphics, getComponent(), 0, 0, SidebarButton.SIZE + 5, GraphicsUtils.font().lineHeight / 2, SkinmaticsConfig.foregroundColor());
        return SidebarButton.SIZE;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (inside(x, y)) {
            increment(-scrollY * 16.0);
            return true;
        }

        for (PanelButton button : buttons) {
            button.mouseScrolled(x, y, scrollX, scrollY);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (event.button() == 0 && inside(event.x(), event.y())) {
            increment(-dy);
            return true;
        }

        for (PanelButton button : buttons) {
            if (button.mouseDragged(event, dx, dy)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (PanelButton button : buttons) {
            if (button.mouseClicked(event, doubleClick)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        for (PanelButton button : buttons) {
            if (button.mouseReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseMoved(double x, double y) {
        for (PanelButton button : buttons) {
            button.mouseMoved(x, y);
        }
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        for (PanelButton button : buttons) {
            if (button.keyPressed(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        for (PanelButton button : buttons) {
            if (button.keyReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        for (PanelButton button : buttons) {
            if (button.charTyped(event)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onClose() {
        for (PanelButton button : buttons) {
            if (button.onClose()) {
                return true;
            }
        }

        return false;
    }
}
