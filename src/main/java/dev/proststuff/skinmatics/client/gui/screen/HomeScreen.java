package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.gui.widget.button.IconButton;
import dev.proststuff.skinmatics.client.gui.widget.button.SimpleBackgroundButton;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import dev.proststuff.utilitary.api.v1.client.gui.Scrollable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SuspiciousMethodCalls")
public abstract class HomeScreen extends AbstractSkinmaticsScreen {
    public static final int SIZE_POWER = 4;
    public static final Identifier SIDEBAR_GROUP = SkinmaticsMod.of("sidebar");
    public static final List<SidebarEntry> SIDEBAR_ENTRIES = new ArrayList<>();

    static {
        SIDEBAR_ENTRIES.add(new SidebarEntry(Component.translatable("skinmatics.config"), SkinmaticsAssets.SKINMATICS_NO_BACKGROUND, (_, _) -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new SkinmaticsConfigScreen(minecraft.screen));
        }));
        SIDEBAR_ENTRIES.add(new SidebarEntry(Component.translatable("skinmatics.profiles.tooltip"), SkinmaticsAssets.PROFILES, (button, _) -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new ProfileScreen(minecraft.screen));
        }));
        SIDEBAR_ENTRIES.add(new SidebarEntry(Component.translatable("skinmatics.textures.tooltip"), SkinmaticsAssets.TEXTURES, (_, _) -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new TextureScreen(minecraft.screen));
        }));
    }

    public final List<Scrollable.Element> scrollables = new ArrayList<>();
    public final List<AbstractButton> sidebarButtons = new ArrayList<>();

    public HomeScreen(Component title, Screen parent) {
        super(title, parent);
    }

    public void populateSidebar(Identifier focusedAs) {
        sidebarButtons.clear();

        for (int y = 0; y < SIDEBAR_ENTRIES.size(); y++) {
            SidebarEntry entry = SIDEBAR_ENTRIES.get(y);
            IconButton button = entry.build(0, 16 * y);
            button.setGroup(SIDEBAR_GROUP);

            sidebarButtons.add(addRenderableWidget(button));

            if (entry.icon.equals(focusedAs)) {
                setFocused(button);
            }
        }
    }

    public void clearScrollables() {
        children().removeIf(scrollables::contains);
        scrollables.clear();
    }

    public abstract Identifier getScreenId();
    public abstract void populateContent();

    @Override
    protected <T extends GuiEventListener & NarratableEntry> @NonNull T addWidget(@NonNull T widget) {
        if (widget instanceof Scrollable.Element) {
            scrollables.add((Scrollable.Element) widget);
        }

        return super.addWidget(widget);
    }

    @Override
    public void init() {
        clearScrollables();
        populateSidebar(getScreenId());
        populateContent();

        applyScroll();
    }

    @Override
    public int getScrollableWidth() {
        return width - INSET * 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(INSET, 0, width - INSET, height, backgroundColor());
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    public record SidebarEntry(Component text, Identifier icon, SimpleBackgroundButton.OnPress<IconButton> onPress) {
        public IconButton build(int x, int y) {
            return IconButton.powerOf2(x, y, icon, SIZE_POWER, onPress).ofTooltip(Tooltip.create(Component.translatable("skinmatics.config"), text)).setScrollDirection(ScrollDirection.LOCKED);
        }
    }
}