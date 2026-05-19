package dev.proststuff.skinmatics.client.gui.element.sidebar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.element.panel.HomePanel;
import dev.proststuff.skinmatics.client.gui.element.sidebar.button.SidebarButton;
import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Sidebar extends AbstractRenderable implements MouseEventListener {
    public static final int SIDEBAR_WIDTH = 16;

    protected final List<SidebarButton> topButtons = new ArrayList<>();
    protected final List<SidebarButton> bottomButtons = new ArrayList<>();

    public Sidebar(Identifier identifier) {
        super(identifier);
        addButton(new SidebarButton(Skinmatics.of("home"), Skinmatics.of("textures/gui/skinmatics.png"), new HomePanel(), false));
        addButton(new SidebarButton(Skinmatics.of("customize"), Skinmatics.of("textures/gui/customize.png"), false));
        addButton(new SidebarButton(Skinmatics.of("skin"), Skinmatics.of("textures/gui/skin.png"), false));
        addButton(new SidebarButton(Skinmatics.of("eyes"), Skinmatics.of("textures/gui/eyes.png"), false));
        addButton(new SidebarButton(Skinmatics.of("cape"), Skinmatics.of("textures/gui/cape.png"), false));
        addButton(new SidebarButton(Skinmatics.of("wings"), Skinmatics.of("textures/gui/wings.png"), false));

        addButton(new SidebarButton(Skinmatics.of("profiles"), Skinmatics.of("textures/gui/profiles.png"), true));
        addButton(new SidebarButton(Skinmatics.of("textures"), Skinmatics.of("textures/gui/textures.png"), true));
        addButton(new SidebarButton(Skinmatics.of("directory"), Skinmatics.of("textures/gui/directory.png"), true));
    }

    public @Nullable SidebarButton getFocused() {
        return SidebarButton.getFocused();
    }

    public @Nullable SidebarButton getHovered() {
        return SidebarButton.getHovered();
    }

    public void addButton(SidebarButton button) {
        if (button.isBottom()) {
            bottomButtons.add(button);
        } else {
            topButtons.add(button);
        }
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        for (SidebarButton button : topButtons) {
            button.init(width, height);
        }

        for (SidebarButton button : bottomButtons) {
            button.init(width, height);
        }

        topButtons.getFirst().focus();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.fill(0, 0, getWidth(), getHeight(), SkinmaticsConfig.backgroundColor());

        int y = 0;
        for (SidebarButton button : topButtons) {
            button.setPosition(0, y);
            button.render(graphics, mouseX, mouseY, tickDelta);
            y += button.getHeight();
        }

        y = getHeight() - SidebarButton.SIZE;

        for (int i = bottomButtons.size() - 1; i >= 0; i--) {
            SidebarButton button = bottomButtons.get(i);

            button.setPosition(0, y);
            button.render(graphics, mouseX, mouseY, tickDelta);
            y -= button.getHeight();
        }
    }

    @Override
    public int getWidth() {
        return SIDEBAR_WIDTH;
    }

    @Override
    public int getHeight() {
        return screenHeight;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (SidebarButton button : topButtons) {
                if (button.mouseClicked(event, doubleClick)) {
                    button.focus();
                    return true;
                }
            }

            for (SidebarButton button : bottomButtons) {
                if (button.mouseClicked(event, doubleClick)) {
                    button.focus();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void mouseMoved(double x, double y) {
        for (SidebarButton button : topButtons) {
            if (button.inside(x, y)) {
                button.hover();
                return;
            }
        }

        for (SidebarButton button : bottomButtons) {
            if (button.inside(x, y)) {
                button.hover();
                return;
            }
        }

        SidebarButton.unhover();
    }
}