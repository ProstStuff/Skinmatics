package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.texture.SkinmaticsAssets;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GroupPanelButton extends AbstractPanelButton {
    protected final List<AbstractPanelButton> children = new ArrayList<>();
    protected boolean showChildren = false;

    public GroupPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    public void toggle() {
        showChildren = !showChildren;
    }

    public void show() {
        showChildren = true;
    }

    public void hide() {
        showChildren = false;
    }

    public boolean shown() {
        return showChildren;
    }

    public <T extends AbstractPanelButton> T add(T button) {
        children.add(button);
        return button;
    }

    public void addAll(AbstractPanelButton button, AbstractPanelButton... buttons) {
        add(button);

        for (AbstractPanelButton panelButton : buttons) {
            add(panelButton);
        }
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        for (AbstractPanelButton button : children) {
            button.init(width, height);
        }
    }

    @Override
    public void setPanel(AbstractPanel panel) {
        super.setPanel(panel);

        for (AbstractPanelButton button : children) {
            button.setPanel(panel);
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        for (AbstractPanelButton button : children) {
            button.onClose();
        }
    }

    @Override
    public int getHeight() {
        Component component = getComponent();
        Font font = GraphicsUtils.font();

        int buttonsHeight = 0;

        if (showChildren) {
            for (AbstractPanelButton button : children) {
                buttonsHeight += button.getHeight();
            }
        }

        return Math.max(super.getHeight(), font.wordWrapHeight(component, AbstractPanel.WIDTH - INDENT) + (INDENT * 2)) + buttonsHeight;
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.press(button, mouseX, mouseY)) {
                    return true;
                }
            }
        }

        if (isFocused() && button == 0 && in(mouseX, mouseY)) {
            toggle();
            return true;
        }

        return !isFocused();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.mouseClicked(event, doubleClick)) {
                    return true;
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.mouseScrolled(x, y, scrollX, scrollY)) {
                    return true;
                }
            }
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.mouseReleased(event)) {
                    return true;
                }
            }
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.mouseDragged(event, dx, dy)) {
                    return true;
                }
            }
        }

        return !isFocused();
    }

    @Override
    public boolean mouseMoved(double x, double y) {
        if (showChildren) {
            for (AbstractPanelButton child : children) {
                if (child.mouseMoved(x, y)) {
                    return true;
                }
            }
        }

        return super.mouseMoved(x, y);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        for (AbstractPanelButton child : children) {
            if (child.keyPressed(event)) {
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        for (AbstractPanelButton child : children) {
            if (child.keyReleased(event)) {
                return true;
            }
        }

        return super.keyReleased(event);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderClickableState(graphics);
        int textY = getY() + (INDENT * 2) - getHeightTextIndent();
        Component component = getComponent();
        int width = getWidth() - (INDENT * 2);
        graphics.textWithWordWrap(GraphicsUtils.font(), component, getX() + INDENT, textY, width, foregroundColor());
        GraphicsUtils.sprite(graphics, showChildren ? SkinmaticsAssets.COLLAPSE : SkinmaticsAssets.EXPAND, getX() + getWidth() - 16, getY(), 16, 16);

        int startY = getY() + Math.max(super.getHeight(), font().wordWrapHeight(component, AbstractPanel.WIDTH - INDENT) + (INDENT * 2));

        if (showChildren) {
            int viewportEndY = getY() + panel.getHeight();

            for (AbstractPanelButton child : children) {
                int height = child.getHeight();
                child.setPosition(getX(), startY);

                if (startY < viewportEndY) {
                    child.render(graphics, mouseX, mouseY, tickDelta);
                }

                startY += height;
            }
        }
    }
}
