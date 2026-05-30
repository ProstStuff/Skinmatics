package dev.proststuff.skinmatics.client.gui.customization;

import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.gui.impl.FileEventListener;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.SidebarButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsClientMod;
import dev.proststuff.skinmatics.client.SkinmaticsClientRegistration;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.customization.avatar.AvatarGUIRenderer;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SkinmaticsCustomizationScreen extends Screen implements SkinmaticsClientMod {
    private final Screen parent;
    private final AvatarGUIRenderer avatarRenderer = new AvatarGUIRenderer();
    private final List<SidebarButton> sidebarButtons = new ArrayList<>();
    private SidebarButton topMostSidebarButton = null;

    public SkinmaticsCustomizationScreen(Screen parent) {
        super(Component.literal("Skinmatics"));
        ClickableRenderable.FOCUSED.clear();
        ClickableRenderable.HOVERED.clear();
        AbstractPanel.TOP_PANEL = null;
        AbstractPanel.BOTTOM_PANEL = null;

        this.parent = parent;

        int bottomIndex = 0;

        for (Map.Entry<SimpleIdentifier, SkinmaticsClientRegistration.ButtonFactory<SidebarButton>> entry : registration().getSidebarButtons().entrySet()) {
            SimpleIdentifier identifier  = entry.getKey();
            SidebarButton button = entry.getValue().create(identifier);

            if (identifier != button.getIdentifier()) {
                throw new IllegalStateException("SidebarButton must have the same identifier as their registered identifer");
            }

            if (button.bottom) {
                sidebarButtons.add(bottomIndex++, button);
            } else {
                sidebarButtons.addLast(button);
            }

            if (topMostSidebarButton == null) {
                topMostSidebarButton = button;
            }
        }
    }

    public void load() {
        SkinmaticsConfig.INSTANCE.load();
        ProfileManager.scanAndLoad();
    }

    public void save() {
        SkinmaticsConfig.INSTANCE.save();
        ProfileManager.saveAll();
    }

    @Override
    protected void init() {
        load();

        for (SidebarButton button : sidebarButtons) {
            button.setScreen(this);
            button.init(width, height);
        }

        if (SidebarButton.FOCUSED.get(topMostSidebarButton.getCategory()) == null) {
            topMostSidebarButton.focus();
        }

        avatarRenderer.setScreen(this);
        avatarRenderer.init(width, height);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        avatarRenderer.render(graphics, mouseX, mouseY, a);
        graphics.fill(0, 0, SidebarButton.WIDTH, height, SkinmaticsConfig.backgroundColor());

        //TODO: Add groupings when too much buttons is rendered
        int y = 0;

        for (SidebarButton button : sidebarButtons) {
            if (!button.bottom) {
                button.setPosition(0, y);
                y += button.getHeight();

                button.render(graphics, mouseX, mouseY, a);
            }
        }

        y =  height - SidebarButton.HEIGHT;

        for (int i = sidebarButtons.size() - 1; i >= 0; i--) {
            SidebarButton button = sidebarButtons.get(i);

            if (button.bottom) {
                button.setPosition(0, y);
                y -= button.getHeight();

                button.render(graphics, mouseX, mouseY, a);
            }
        }

        if (AbstractPanel.TOP_PANEL != null) {
            AbstractPanel.TOP_PANEL.setPosition(SidebarButton.WIDTH, 0);
            AbstractPanel.TOP_PANEL.render(graphics, mouseX, mouseY, a);
        }

        if (AbstractPanel.BOTTOM_PANEL != null) {
            if (AbstractPanel.TOP_PANEL != null) {
                AbstractPanel.BOTTOM_PANEL.setPosition(SidebarButton.WIDTH, height / 2);
            } else {
                AbstractPanel.BOTTOM_PANEL.setPosition(SidebarButton.WIDTH, 0);
            }

            AbstractPanel.BOTTOM_PANEL.render(graphics, mouseX, mouseY, a);
        }
    }

    @Override
    public void mouseMoved(double x, double y) {
        for (SidebarButton button : sidebarButtons) {
            button.mouseMoved(x, y);
        }

        if (AbstractPanel.TOP_PANEL != null) {
            AbstractPanel.TOP_PANEL.mouseMoved(x, y);
        }

        if (AbstractPanel.BOTTOM_PANEL != null) {
            AbstractPanel.BOTTOM_PANEL.mouseMoved(x, y);
        }

        avatarRenderer.mouseMoved(x, y);

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (SidebarButton button : sidebarButtons) {
            if (button.mouseClicked(event, doubleClick)) {
                return true;
            }
        }

        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (avatarRenderer.mouseClicked(event, doubleClick)) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (avatarRenderer.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.mouseDragged(event, dx, dy)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.mouseDragged(event, dx, dy)) {
            return true;
        }

        if (avatarRenderer.mouseDragged(event, dx, dy)) {
            return true;
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        avatarRenderer.mouseReleased(event);

        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.mouseReleased(event)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.mouseReleased(event)) {
            return true;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.keyPressed(event)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.keyReleased(event)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.keyReleased(event)) {
            return true;
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (AbstractPanel.TOP_PANEL != null && AbstractPanel.TOP_PANEL.charTyped(event)) {
            return true;
        }

        if (AbstractPanel.BOTTOM_PANEL != null && AbstractPanel.BOTTOM_PANEL.charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public void onFilesDrop(@NonNull List<Path> files) {
        if (AbstractPanel.TOP_PANEL instanceof FileEventListener fileEventListener) {
            fileEventListener.onFilesDropped(files);
        }

        if (AbstractPanel.BOTTOM_PANEL instanceof FileEventListener fileEventListener) {
            fileEventListener.onFilesDropped(files);
        }
    }

    @Override
    public void onClose() {
        for (SidebarButton sidebarButton : sidebarButtons) {
            sidebarButton.onClose();
        }

        if (AbstractPanel.TOP_PANEL != null) {
            AbstractPanel.TOP_PANEL.onClose();
        }

        if (AbstractPanel.BOTTOM_PANEL != null) {
            AbstractPanel.BOTTOM_PANEL.onClose();
        }

        avatarRenderer.onClose();

        save();

        if (minecraft.level == null) {
            ProfileManager.clear();
            ProfileManager.unassign(ProfileManager.getLocalUUID());
        }

        minecraft.setScreen(parent);

        ClickableRenderable.FOCUSED.clear();
        ClickableRenderable.HOVERED.clear();
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}
