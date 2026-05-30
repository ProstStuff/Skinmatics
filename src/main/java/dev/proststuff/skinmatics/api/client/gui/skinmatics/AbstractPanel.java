package dev.proststuff.skinmatics.api.client.gui.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.impl.KeyEventListener;
import dev.proststuff.skinmatics.api.client.gui.impl.MouseEventListener;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.AbstractPanelButton;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractPanel extends AbstractSkinmaticsRenderable implements MouseEventListener, KeyEventListener {
    public static AbstractPanel TOP_PANEL = null;
    public static AbstractPanel BOTTOM_PANEL = null;
    public static final int WIDTH = 256;

    protected final List<AbstractPanelButton> buttons = new ArrayList<>();
    protected final boolean bottom;

    protected double scrollingPosition = 0;
    protected SidebarButton source;

    public AbstractPanel(SimpleIdentifier identifier, boolean bottom) {
        super(identifier);
        this.bottom = bottom;
    }

    public void setSource(SidebarButton source) {
        this.source = source;
    }

    public SidebarButton getSource() {
        return source;
    }

    public List<AbstractPanelButton> getButtons() {
        return buttons;
    }

    public <B extends AbstractPanelButton> B addButton(B button) {
        buttons.add(button);
        button.setPanel(this);
        return button;
    }

    public void addButtons(AbstractPanelButton button, AbstractPanelButton... panelButtons) {
        addButton(button);

        for (AbstractPanelButton panelButton : panelButtons) {
            addButton(panelButton);
        }
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        scrollingPosition = 0;
        setPosition(SidebarButton.WIDTH, 0);

        int currentY = 16 - (int) scrollingPosition;

        for (AbstractPanelButton button : buttons) {
            button.setPanel(this);
            button.setPosition(getX(), currentY);
            button.init(width, height);

            currentY += button.getHeight();
        }

        AbstractPanelButton button = !buttons.isEmpty() ? buttons.getFirst() : null;
        if (!shouldUnfocusWhenClicked() && button != null && !AbstractPanelButton.FOCUSED.containsKey(button.getCategory())) {
            button.focus();
        }
    }

    public int getContentHeight() {
        int height = source != null ? SidebarButton.HEIGHT : 0;

        for (AbstractPanelButton button : buttons) {
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
        int maxScroll = Math.max(0, getContentHeight() - visibleHeight);

        if (scrollingPosition > maxScroll) {
            scrollingPosition = maxScroll;
        }
    }

    public boolean shouldUnfocusWhenClicked() {
        return true;
    }

    public int renderTitleBar(GuiGraphicsExtractor graphics) {
        graphics.fill(SidebarButton.WIDTH, getY(), SidebarButton.WIDTH + AbstractPanel.WIDTH, getY() + SidebarButton.HEIGHT, SkinmaticsConfig.accentColor());
        graphics.verticalLine(SidebarButton.WIDTH, getY() - 1, getY() + SidebarButton.HEIGHT, SkinmaticsConfig.foregroundColor());
        GraphicsUtils.text(graphics, getComponent(), 0, 0, SidebarButton.WIDTH + 5, getY() + SidebarButton.HEIGHT / 2 - GraphicsUtils.font().lineHeight / 2, SkinmaticsConfig.foregroundColor());

        return SidebarButton.HEIGHT;
    }

    public void renderButtons(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, int startY) {
        int viewportTop = getY();
        int viewportBottom = getY() + getHeight();

        graphics.enableScissor(SidebarButton.WIDTH, viewportTop + 16, SidebarButton.WIDTH + getWidth(), viewportBottom);

        int currentY = getY() + startY - (int) scrollingPosition;

        for (AbstractPanelButton button : buttons) {
            int buttonTop = currentY;
            int buttonBottom = currentY + button.getHeight();
            boolean visible = buttonBottom >= viewportTop && buttonTop <= viewportBottom;

            if (visible) {
                button.setPosition(getX(), currentY);
                button.render(graphics, mouseX, mouseY, tickDelta);
            }

            currentY += button.getHeight();
        }

        graphics.disableScissor();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderBackground(graphics);
        renderTitleBar(graphics);
        renderButtons(graphics, mouseX, mouseY, tickDelta, renderTitleBar(graphics));
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        int div;

        if (bottom) {
            div = TOP_PANEL != null ? 2 : 1;
        } else {
            div = BOTTOM_PANEL != null ? 2 : 1;
        }

        return getScreenHeight() / div;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean activated = false;
        for (AbstractPanelButton button : buttons) {
            if (activated || !button.mouseClicked(event, doubleClick)) {
                if (shouldUnfocusWhenClicked()) button.unfocus();
            } else {
                activated = true;
            }
        }

        return activated;
    }

    @Override
    public boolean mouseMoved(double x, double y) {
        for (AbstractPanelButton button : buttons) {
            if (button.mouseMoved(x, y)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        for (AbstractPanelButton button : buttons) {
            if (button.mouseReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        for (AbstractPanelButton button : buttons) {
            if (button.mouseDragged(event, dx, dy)) {
                return true;
            }
        }

        if (event.button() == 0 && in(event.x(), event.y())) {
            increment(-dy);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        for (AbstractPanelButton button : buttons) {
            if (button.mouseScrolled(x, y, scrollX, scrollY)) {
                return true;
            }
        }

        if (in(x, y)) {
            increment(-scrollY * 16.0);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        for (AbstractPanelButton button : buttons) {
            if (button.keyPressed(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        for (AbstractPanelButton button : buttons) {
            if (button.keyReleased(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        for (AbstractPanelButton button : buttons) {
            if (button.charTyped(event)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClose() {
        for (AbstractPanelButton button : buttons) {
            button.onClose();
        }
    }
}