package dev.proststuff.skinmatics.client.gui.panel;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.Clickable;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import dev.proststuff.skinmatics.client.gui.impl.Renderable;
import dev.proststuff.skinmatics.client.gui.impl.ScreenEventListener;
import dev.proststuff.skinmatics.client.gui.impl.ScreenTracker;
import dev.proststuff.skinmatics.client.gui.panel.clickable.PanelClickable;
import dev.proststuff.skinmatics.client.gui.sidebar.SidebarClickable;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Panel implements ScreenEventListener, ScreenTracker, Renderable {
    protected final SkinmaticsConfig config = SkinmaticsConfig.INSTANCE;
    protected final SidebarClickable sidebarClickable;
    protected final List<PanelClickable<?>> buttons = new ArrayList<>();
    protected int width = 0;
    protected int height = 0;
    protected double scrollingPosition = 0;

    public Panel(SidebarClickable sidebarClickable) {
        this.sidebarClickable = sidebarClickable;
    }

    public void addClickable(PanelClickable<?> clickable) {
        buttons.add(clickable);
    }

    public int getClickableHeight() {
        int height = 0;

        for (PanelClickable<?> clickable : buttons) {
            height += clickable.getHeight();
        }

        return height;
    }

    @Override
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;

        for (PanelClickable<?> button : buttons) {
            button.setDimensions(width, height);
        }

        increment(0.0F);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        int titleBarHeight = renderTitleBar(graphics);
        int y = elementY + titleBarHeight - (int) scrollingPosition;
        graphics.enableScissor(elementX, titleBarHeight, elementX + CustomizationScreen.PANEL_SIZE, height);

        for (PanelClickable<?> button : buttons) {
            if (y + button.getHeight() >= titleBarHeight && y <= height) {
                button.setY(y);
                button.render(graphics, mouseX, mouseY, elementX, y);
            }

            y += button.getHeight();
        }

        graphics.disableScissor();
    }

    public int renderTitleBar(GuiGraphicsExtractor graphics) {
        int sidebar = CustomizationScreen.SIDEBAR_SIZE;
        int panel = CustomizationScreen.PANEL_SIZE;

        graphics.fill(sidebar, 0, sidebar + panel, sidebar, SkinmaticsConfig.accentColor());
        graphics.fill(sidebar, 0, sidebar + 1, sidebar, SkinmaticsConfig.foregroundColor());
        GraphicsUtils.text(graphics, sidebarClickable.asComponent(), 0, 0, sidebar + 5, GraphicsUtils.font().lineHeight / 2, SkinmaticsConfig.foregroundColor());
        return sidebar;
    }

    public void increment(double deltaY) {
        scrollingPosition += deltaY;

        if (scrollingPosition < 0) {
            scrollingPosition = 0;
        }

        int visibleHeight = height - CustomizationScreen.SIDEBAR_SIZE;
        int maxScroll = Math.max(0, getClickableHeight() - visibleHeight);

        if (scrollingPosition > maxScroll) {
            scrollingPosition = maxScroll;
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (insidePanel(x, y)) {
            increment(-scrollY * 16.0);
            return true;
        }

        for (Clickable button : buttons) {
            button.mouseScrolled(x, y, scrollX, scrollY);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (insidePanel(event.x(), event.y())) {
            increment(-dy);
            return true;
        }

        for (Clickable button : buttons) {
            if (button.mouseDragged(event, dx, dy)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (Clickable button : buttons) {
            if (button.mouseClicked(event, doubleClick)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        for (Clickable button : buttons) {
            if (button.mouseReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseMoved(double x, double y) {
        for (Clickable button : buttons) {
            button.mouseMoved(x, y);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        for (Clickable button : buttons) {
            if (button.keyPressed(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        for (Clickable button : buttons) {
            if (button.keyReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (Clickable button : buttons) {
            if (button.charTyped(event)) {
                return true;
            }
        }
        return false;
    }

    public boolean insidePanel(double pointX, double pointY) {
        return inside(pointX, pointY, CustomizationScreen.SIDEBAR_SIZE, 0, CustomizationScreen.PANEL_SIZE, height);
    }

    public static boolean inside(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
    }

    @Override
    public int hashCode() {
        return sidebarClickable.hashCode();
    }
}