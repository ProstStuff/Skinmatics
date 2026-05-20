package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.api.skinmatics.ProfileHandler;
import dev.proststuff.skinmatics.api.skinmatics.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.element.sidebar.Sidebar;
import dev.proststuff.skinmatics.client.gui.element.sidebar.button.SidebarButton;
import dev.proststuff.skinmatics.client.gui.impl.FileEventListener;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;

public class CustomizationScreen extends Screen {
    protected final Screen parent;
    protected final Sidebar sidebar = new Sidebar();
    protected final AvatarPreviewRenderer avatarPreview = new AvatarPreviewRenderer();

    protected Panel panel;

    public CustomizationScreen(Screen parent) {
        super(Component.literal("skinmatics"));
        this.parent = parent;
    }

    public void load() {
        SkinmaticsConfig.INSTANCE.load();
        //ProfileHandler.walkProfiles();
        //ProfileHandler.loadProfiles();
        ProfileHandler.loadCurrentProfile(minecraft.player);
    }

    public void save() {
        SkinmaticsConfig.INSTANCE.save();
        ProfileHandler.saveProfiles();

        load();
    }

    @Override
    protected void init() {
        SidebarButton.reset();

        load();
        sidebar.init(width, height);
        avatarPreview.init(width, height);
        avatarPreview.setPosition(Sidebar.SIDEBAR_WIDTH + Panel.WIDTH, 0);

        if (panel == null && sidebar.getFocused() != null && sidebar.getFocused().getPanel() != null) {
            panel = sidebar.getFocused().getPanel();
        }

        panel.init(width, height);

        if (minecraft.level != null) {
            avatarPreview.setUpAvatar(minecraft.level);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        avatarPreview.render(graphics, mouseX, mouseY, a);
        sidebar.render(graphics, mouseX, mouseY, a);

        if (panel != null) {
            panel.render(graphics, mouseX, mouseY, a);
        }

        GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.disclaimer.wip"), 0, width / 2, GraphicsUtils.font().lineHeight, SkinmaticsConfig.foregroundColor(0.25F));
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (sidebar.mouseClicked(event, doubleClick)) {
            SidebarButton focused = sidebar.getFocused();
            if (focused != null && focused.getPanel() != null) {
                this.panel = focused.getPanel();
                panel.init(width, height);
            }

            return true;
        }

        if (avatarPreview.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (panel != null) {
            if (panel.mouseClicked(event, doubleClick)) {
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void mouseMoved(double x, double y) {
        sidebar.mouseMoved(x, y);

        if (panel != null) {
            panel.mouseMoved(x, y);
        }

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (avatarPreview.mouseDragged(event, dx, dy)) {
            return true;
        }

        if (panel != null) {
            if (panel.mouseDragged(event, dx, dy)) {
                return true;
            }
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (avatarPreview.mouseReleased(event)) {
            return true;
        }

        if (panel != null) {
            if (panel.mouseReleased(event)) {
                return true;
            }
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (panel != null) {
            if (panel.mouseScrolled(x, y, scrollX, scrollY)) {
                return true;
            }
        }

        if (avatarPreview.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (panel != null) {
            if (panel.keyPressed(event)) {
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        if (panel != null) {
            if (panel.keyReleased(event)) {
                return true;
            }
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (panel != null) {
            if (panel.charTyped(event)) {
                return true;
            }
        }

        return super.charTyped(event);
    }

    @Override
    public void onFilesDrop(@NonNull List<Path> files) {
        if (panel instanceof FileEventListener eventListener) {
            eventListener.onFilesDropped(files);
        }
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void onClose() {
        save();

        if (panel != null) {
            if (panel.onClose()) {
                return;
            }
        }

        SidebarButton.reset();
        minecraft.setScreen(parent);
    }
}
