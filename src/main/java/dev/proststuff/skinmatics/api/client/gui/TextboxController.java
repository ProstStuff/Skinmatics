package dev.proststuff.skinmatics.api.client.gui;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.api.client.impl.gui.GuiRenderable;
import dev.proststuff.skinmatics.api.client.impl.gui.KeyEventListener;
import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import dev.proststuff.skinmatics.api.client.impl.gui.ScreenEventListener;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

// TODO: Move this to Utilitary
public class TextboxController implements MouseEventListener, KeyEventListener, ScreenEventListener, GuiRenderable {
    public static int PADDING = 4;

    protected String text;
    protected boolean editable = true;
    protected int cursor = 0;
    protected boolean focused = false;

    protected final int width;

    protected int x = 0;
    protected int y = 0;

    protected int screenWidth = 0;
    protected int screenHeight = 0;

    public TextboxController(String text, int width) {
        this.text = text;
        this.width = width;

        this.cursor = text.length();
    }

    public TextboxController(int width) {
        this("", width);
    }

    public void set(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }

    public void uneditable() {
        this.editable = false;
    }

    public void editable() {
        this.editable = true;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isFocused() {
        return focused;
    }

    public int getWidth() {
        return width;
    }

    public int getTextWidth() {
        return GraphicsUtils.font().width(text);
    }

    public int getTextHeight() {
        return GraphicsUtils.font().wordWrapHeight(Component.literal(text), width - PADDING * 2);
    }

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        focused = event.button() == 0 && inside(event.x(), event.y());
        if (focused) cursor = text.length();

        return focused;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!(focused && editable)) return false;
        int key = event.key();

        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (cursor > 0) {
                    text = text.substring(0, cursor - 1) + text.substring(cursor);
                    cursor--;
                }

                return true;
            }

            case GLFW.GLFW_KEY_DELETE -> {
                if (cursor < text.length()) text = text.substring(0, cursor) + text.substring(cursor + 1);
                return true;
            }

            case GLFW.GLFW_KEY_LEFT -> {
                cursor = Math.max(0, cursor - 1);
                return true;
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                cursor = Math.min(text.length(), cursor + 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (!(focused && editable)) return false;
        int c = event.codepoint();
        if (Character.isISOControl(c)) return false;

        text = text.substring(0, cursor) + Character.toString(c) + text.substring(cursor);
        cursor++;
        return true;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        render(graphics, mouseX, mouseY, tickDelta, SkinmaticsConfig.foregroundColor());
    }

    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, int color) {
        Font font = GraphicsUtils.font();
        int drawY = y;

        List<String> lines = font.getSplitter().splitLines(text, width - (PADDING * 2), Style.EMPTY).stream().map(FormattedText::getString).toList();
        int remainingCursor = cursor;

        for (String line : lines) {
            graphics.text(font, line, x + PADDING, drawY, color);

            if (focused && remainingCursor >= 0) {
                boolean cursorInThisLine = remainingCursor <= line.length();

                if (cursorInThisLine) {
                    boolean visible = ((System.currentTimeMillis() / 500) % 2 == 0);

                    if (visible) {
                        String beforeCursor = line.substring(0, remainingCursor);
                        int cursorX = x + PADDING + font.width(beforeCursor);

                        graphics.fill(cursorX, drawY, cursorX + 1, drawY + font.lineHeight, color);
                    }

                    remainingCursor = -1;
                } else {
                    remainingCursor -= line.length();
                }
            }

            drawY += font.lineHeight;
        }

        if (focused && text.isEmpty()) {
            boolean visible = ((System.currentTimeMillis() / 500) % 2 == 0);
            if (visible) {
                graphics.fill(x + PADDING, y + PADDING, x + PADDING + 1, y + PADDING + font.lineHeight, color);
            }
        }
    }

    public boolean inside(double pointX, double pointY) {
        return pointX >= x && pointX < x + width && pointY >= y && pointY < y + getTextHeight();
    }
}