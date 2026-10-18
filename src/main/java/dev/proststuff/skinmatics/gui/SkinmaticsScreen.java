package dev.proststuff.skinmatics.gui;

import dev.proststuff.skinmatics.gui.widget.CloseScreenButton;
import dev.proststuff.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.utilitary.client.gui.MultiFocusScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class SkinmaticsScreen extends MultiFocusScreen implements SkinmaticsGui {
    public static final int BAR_HEIGHT = 16;
    public static final int BAR_X_INSET = 8;
    public static final float BACKGROUND_ALPHA = 0.5F;
    public static final float OUTLINE_ALPHA = 0.25F;

    public static final int HORIZONTAL_INSET = 8;
    public static final int VERTICAL_INSET = 24;
    public static final int SPACING = 8;

    protected final Screen parent;

    protected UiRect screenRect = UiRect.ZERO;
    protected UiRect contentRect = UiRect.ZERO;
    protected UiRect topBarRect = UiRect.ZERO;
    protected UiRect bottomBarRect = UiRect.ZERO;

    protected SkinmaticsScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void init() {
        this.screenRect = screenRect.with(0, 0, width, height);
        this.contentRect = screenRect.with(HORIZONTAL_INSET, VERTICAL_INSET, width - HORIZONTAL_INSET, height - VERTICAL_INSET);
        this.topBarRect = screenRect.withY1(BAR_HEIGHT);
        this.bottomBarRect = screenRect.withY0(height - BAR_HEIGHT);
        addRenderableWidget(new CloseScreenButton(width - 16, 0));
    }

    protected void extractPanelBackground(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1) {
        graphics.fill(x0, y0, x1, y1, backgroundColor(BACKGROUND_ALPHA));
        int outlineX1 = x1 - 1;

        if (y0 > 0) {
            graphics.horizontalLine(x0, outlineX1, y0 - 1, foregroundColor(OUTLINE_ALPHA));
            graphics.horizontalLine(x0, outlineX1, y0, backgroundColor(OUTLINE_ALPHA));
        }

        if (y1 <= height) {
            graphics.horizontalLine(x0, outlineX1, y1 - 1, backgroundColor(OUTLINE_ALPHA));
            graphics.horizontalLine(x0, outlineX1, y1, foregroundColor(OUTLINE_ALPHA));
        }
    }

    protected void extractTopBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPanelBackground(graphics, 0, 0, width, BAR_HEIGHT);
        graphics.text(font, getTitle(), BAR_X_INSET, BAR_HEIGHT / 2 - font.lineHeight / 2, foregroundColor());
    }

    public void extractBottomBar(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPanelBackground(graphics, 0, height - BAR_HEIGHT, width, height);
    }

    @Override
    public void onClose() {
        SkinmaticsConfig.save();
        minecraft.setScreen(parent);
    }

    public record UiRect(int x0, int y0, int x1, int y1) {
        public static final UiRect ZERO = new UiRect(0, 0, 0, 0);

        public UiRect with(int x0, int y0, int x1, int y1) {
            return new UiRect(x0, y0, x1, y1);
        }

        public UiRect withX(int x0, int x1) {
            return new UiRect(x0, y0, x1, y1);
        }

        public UiRect withY(int y0, int y1) {
            return new UiRect(x0, y0, x1, y1);
        }

        public UiRect withX0(int x0) {
            return withX(x0, x1);
        }

        public UiRect withY0(int y0) {
            return withY(y0, y1);
        }

        public UiRect withX1(int x1) {
            return withX(x0, x1);
        }

        public UiRect withY1(int y1) {
            return withY(y0, y1);
        }

        public int getCenterX() {
            return x0 + getWidth() / 2;
        }

        public int getCenterY() {
            return y0 + getHeight() / 2;
        }

        public int getWidth() {
            return x1 - x0;
        }

        public int getHeight() {
            return y1 - y0;
        }

        public boolean contains(int mouseX, int mouseY) {
            return mouseX > x0 && mouseY > y0 && mouseX <= x1 && mouseY <= y1;
        }

        public boolean contains(double mouseX, double mouseY) {
            return mouseX > x0 && mouseY > y0 && mouseX <= x1 && mouseY <= y1;
        }

        public void extractBorder(GuiGraphicsExtractor graphics, int color) {
            graphics.outline(x0, y0, getWidth() - 1, getHeight() - 1, color);
        }

        public void extractCenterPoint(GuiGraphicsExtractor graphics, int color) {
            int cx = getCenterX();
            int cy = getCenterY();
            graphics.outline(cx - 1, cy - 1, 2, 2, color);
        }
    }
}
