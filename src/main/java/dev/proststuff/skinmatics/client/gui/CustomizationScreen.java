package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.panel.Panel;
import dev.proststuff.skinmatics.client.gui.sidebar.ProfileClickable;
import dev.proststuff.skinmatics.client.gui.sidebar.SidebarClickable;
import dev.proststuff.skinmatics.client.gui.sidebar.SkinmaticsClickable;
import dev.proststuff.skinmatics.client.skinmatics.ProfileHandler;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// GUI is pain...
// GUI is hell....
// GUI is..... Oh, it works-ah never mind, it breaks.
public class CustomizationScreen extends Screen {
    public static final int SIDEBAR_SIZE = 16;
    public static final int PANEL_SIZE = 128;

    protected final Screen parent;
    protected final SkinmaticsConfig config = SkinmaticsConfig.INSTANCE;

    protected final List<SidebarClickable> sidebarTopButtons = new ArrayList<>();
    protected final List<SidebarClickable> sidebarBottomButtons = new ArrayList<>();

    public CustomizationScreen(Screen parent) {
        super(Component.literal("skinmatics"));
        this.parent = parent;

        sidebarTopButtons.add(new SkinmaticsClickable());
        sidebarTopButtons.add(new ProfileClickable());
        //sidebarTopButtons.add(new SidebarClickable(Skinmatics.of("textures"), Skinmatics.of("textures/gui/textures.png")));
        //sidebarBottomButtons.add(new SidebarClickable(Skinmatics.of("directory"), Skinmatics.of("textures/gui/directory.png")));
    }

    @Override
    protected void init() {
        SkinmaticsConfig.INSTANCE.load();
        ProfileHandler.walkProfiles();
        ProfileHandler.loadProfiles();

        if (minecraft.player != null) {
            ProfileHandler.loadCurrentProfile(minecraft.player);
        }

        if (Clickable.getFocused(Clickable.ButtonType.SIDEBAR) == null) {
            setFocused(sidebarTopButtons.getFirst());
        }

        getPanel().setDimensions(width, height);
    }

    public Panel getPanel() {
        return ((SidebarClickable) Clickable.getFocused(Clickable.ButtonType.SIDEBAR)).getPanel();
    }

    public void setFocused(Clickable clickable) {
        clickable.setFocused();

        if (clickable instanceof SidebarClickable sidebarClickable) {
            sidebarClickable.getPanel().setDimensions(width, height);
        }
    }

    public @Nullable Clickable getButtonAt(double x, double y) {
        for (int i = 0; i < sidebarTopButtons.size(); i++) {
            Clickable clickable = sidebarTopButtons.get(i);
            int buttonY = i * SIDEBAR_SIZE;

            if (inside(x, y, 0, buttonY, SIDEBAR_SIZE, SIDEBAR_SIZE)) return clickable;
        }

        for (int i = 0; i < sidebarBottomButtons.size(); i++) {
            Clickable clickable = sidebarBottomButtons.get(i);
            int buttonY = height - (i + 1) * SIDEBAR_SIZE;
            if (inside(x, y, 0, buttonY, SIDEBAR_SIZE, SIDEBAR_SIZE)) return clickable;
        }

        return null;
    }

    public @Nullable Clickable getButtonAt(int x, int y) {
        return getButtonAt((double) x, y);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.fill(0, 0, SIDEBAR_SIZE + PANEL_SIZE, height, SkinmaticsConfig.backgroundColor());

        for (int i = 0; i < sidebarTopButtons.size(); i++) {
            SidebarClickable clickable = sidebarTopButtons.get(i);
            int y = i * SIDEBAR_SIZE;
            clickable.render(graphics, mouseX, mouseY, 0, y);
        }

        for (int i = 0; i < sidebarBottomButtons.size(); i++) {
            SidebarClickable clickable = sidebarBottomButtons.get(i);
            int y = height - (i + 1) * SIDEBAR_SIZE;
            clickable.render(graphics, mouseX, mouseY, 0, y);
        }

        GraphicsUtils.centerText(graphics, Component.translatable("skinmatics.disclaimer.wip"), 0.0F, width / 2, font.lineHeight, SkinmaticsConfig.foregroundColor(0.125F));
        graphics.textWithWordWrap(font, Component.translatable("skinmatics.disclaimer.info"), width / 2 - width / 8, font.lineHeight * 2, width / 4, SkinmaticsConfig.foregroundColor(0.125F));
    }

    @Override
    public void mouseMoved(double x, double y) {
        Clickable clickable = getButtonAt(x, y);

        if (clickable != null && clickable.getType() == Clickable.ButtonType.SIDEBAR && clickable instanceof SidebarClickable) {
            clickable.setHovered();
        } else {
            Clickable.unhover(Clickable.ButtonType.SIDEBAR);
        }

        if (getPanel() != null) {
            getPanel().mouseMoved(x, y);
        }

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        Clickable clickable = getButtonAt(event.x(), event.y());

        if (clickable != null && clickable.getType() == Clickable.ButtonType.SIDEBAR && clickable instanceof SidebarClickable) {
            setFocused(clickable);
            return true;
        }

        Clickable.unhover(Clickable.ButtonType.PANEL);
        if (getPanel() != null && getPanel().mouseClicked(event, doubleClick)) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (getPanel() != null && getPanel().mouseDragged(event, dx, dy)) {
            return true;
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (getPanel() != null && getPanel().mouseScrolled(x, y, scrollX, scrollY)) {
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (getPanel() != null && getPanel().mouseReleased(event)) {
            return true;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (getPanel() != null && getPanel().keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {
        if (getPanel() != null && getPanel().keyReleased(event)) {
            return true;
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (getPanel() != null && getPanel().charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        SkinmaticsConfig.INSTANCE.save();
        ProfileHandler.saveProfiles();
        ProfileHandler.walkProfiles();
        ProfileHandler.loadProfiles();

        if (minecraft.player != null) {
            ProfileHandler.loadCurrentProfile(minecraft.player);
        }

        minecraft.setScreen(parent);

        Clickable.unhover(Clickable.ButtonType.SIDEBAR);
        Clickable.unhover(Clickable.ButtonType.PANEL);
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    public static boolean inside(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
    }
}
