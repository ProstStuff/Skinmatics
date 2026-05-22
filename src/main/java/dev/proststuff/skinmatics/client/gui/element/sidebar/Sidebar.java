package dev.proststuff.skinmatics.client.gui.element.sidebar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.api.SkinmaticsConfig;
import dev.proststuff.skinmatics.api.utility.SkinmaticsAssets;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.element.SimpleButton;
import dev.proststuff.skinmatics.client.gui.element.panel.HomePanel;
import dev.proststuff.skinmatics.client.gui.element.panel.ProfilesPanel;
import dev.proststuff.skinmatics.client.gui.element.sidebar.button.SidebarButton;
import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Sidebar extends AbstractRenderable implements MouseEventListener {
    public static final int SIDEBAR_WIDTH = 16;

    protected final List<SidebarButton> topButtons = new ArrayList<>();
    protected final List<SidebarButton> bottomButtons = new ArrayList<>();

    public Sidebar() {
        super(Skinmatics.of("sidebar"));
        addButton(new SidebarButton(Skinmatics.ofSimple("home"), SkinmaticsAssets.SKINMATICS, new HomePanel(), false));
        addButton(new SidebarButton(Skinmatics.ofSimple("customize"), SkinmaticsAssets.CUSTOMIZE, false));
        addButton(new SidebarButton(Skinmatics.ofSimple("skin"), SkinmaticsAssets.SKIN, false));
        addButton(new SidebarButton(Skinmatics.ofSimple("eyes"), SkinmaticsAssets.EYES, false));
        addButton(new SidebarButton(Skinmatics.ofSimple("cape"), SkinmaticsAssets.CAPE, false));
        addButton(new SidebarButton(Skinmatics.ofSimple("wings"), SkinmaticsAssets.WINGS, false));

        addButton(new SidebarButton(Skinmatics.ofSimple("profiles"), SkinmaticsAssets.PROFILES, new ProfilesPanel(), true));
        addButton(new SidebarButton(Skinmatics.ofSimple("textures"), SkinmaticsAssets.TEXTURES, true));
        addButton(new SidebarButton(Skinmatics.ofSimple("directory"), SkinmaticsAssets.DIRECTORY, true));
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

        SimpleButton.unhover();
    }
}