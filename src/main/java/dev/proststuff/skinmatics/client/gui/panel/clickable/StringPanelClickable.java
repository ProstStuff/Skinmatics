package dev.proststuff.skinmatics.client.gui.panel.clickable;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import dev.proststuff.utilitary.serialization.content.ConfigField;
import dev.proststuff.utilitary.serialization.content.field.StringConfigField;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class StringPanelClickable extends PanelClickable<String> {
    protected String text;
    protected boolean focused = false;
    protected int cursorPosition;
    protected boolean editable = true;
    protected StringConfigField configField = null;

    public StringPanelClickable(Identifier identifier, String text) {
        super(identifier);
        this.text = text;
        this.cursorPosition = text.length();
    }

    public StringPanelClickable(Identifier identifier, StringConfigField configField) {
        super(identifier);
        this.configField = configField;
        this.text = this.configField.get();
    }

    public StringPanelClickable(Identifier identifier) {
        this(identifier, "");
    }

    @Override
    public ConfigField<String> getField() {
        return configField;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        Font font = GraphicsUtils.font();
        graphics.textWithWordWrap(font, asComponent(), elementX + 4, elementY + font.lineHeight / 2, CustomizationScreen.PANEL_SIZE / 2 - 4, SkinmaticsConfig.foregroundColor());

        Component component = getTextComponent();
        int panelSize = CustomizationScreen.PANEL_SIZE;
        int x = elementX + (panelSize / 2);
        int textY = elementY + font.lineHeight / 2;
        graphics.textWithWordWrap(font, component, x, textY, panelSize / 2 - 4, SkinmaticsConfig.foregroundColor());

        if (isFocused() || isHovered()) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                int wrapWidth = panelSize / 2 - 4;
                String beforeCursor = text.substring(0, cursorPosition);
                int lineIndex = getCursorLineIndex(font, text, wrapWidth, cursorPosition);
                int cursorOffsetX = lineWordWrapWidth(font, beforeCursor, wrapWidth, lineIndex);
                int cursorX = x + cursorOffsetX;
                int cursorY = textY + (lineIndex * font.lineHeight);

                graphics.fill(cursorX, cursorY, cursorX + 1, cursorY + font.lineHeight, SkinmaticsConfig.foregroundColor());
            }
        }
    }

    public String getText() {
        return getField() != null ? getField().get() : text;
    }

    public Component getTextComponent() {
        return Component.literal(text);
    }

    public void setText(String text) {
        if (getField() != null) {
            getField().set(text);
            this.text = getField().get();
        } else {
            this.text = text;
        }

        this.cursorPosition = text.length();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (inside(event.x(), event.y())) {
            if (!isFocused()) {
                this.cursorPosition = text.length();
            }

            setFocused();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused() || !editable) {
            return false;
        }

        int key = event.key();

        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }

            return true;
        }

        if (key == GLFW.GLFW_KEY_LEFT) {
            if (cursorPosition > 0) {
                cursorPosition--;
            }

            return true;
        }

        if (key == GLFW.GLFW_KEY_RIGHT) {
            if (cursorPosition < text.length()) {
                cursorPosition++;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!isFocused() || !editable) {
            return false;
        }

        int c = event.codepoint();

        if (Character.isISOControl(c)) {
            return false;
        }

        text = text.substring(0, cursorPosition) + Character.toString(event.codepoint()) + text.substring(cursorPosition);
        cursorPosition ++;

        return true;
    }

    @Override
    public int getHeight() {
        Component nameComponent = asComponent();
        Component textComponent = getTextComponent();
        Font font = GraphicsUtils.font();
        int width = (CustomizationScreen.PANEL_SIZE / 2) - 4;
        return Math.max(font.wordWrapHeight(nameComponent, width), font.wordWrapHeight(textComponent, width)) + font.lineHeight / 2;
    }

    //TODO: Migrate this to Utilitary library
    public static int getCursorLineIndex(Font font, String text, int textWidth, int cursorPosition) {
        String beforeCursor = text.substring(0, cursorPosition);
        List<String> wrappedLines = font.getSplitter().splitLines(beforeCursor, textWidth, Style.EMPTY).stream().map(FormattedText::getString).toList();
        return Math.max(0, wrappedLines.size() - 1);
    }

    //TODO: Migrate this to Utilitary library
    public static int lineWordWrapWidth(Font font, String text, int textWidth, int lineIndex) {
        List<String> wrappedLines = font.getSplitter().splitLines(text, textWidth, Style.EMPTY).stream().map(FormattedText::getString).toList();
        lineIndex = Math.min(lineIndex, wrappedLines.size() - 1);
        return wrappedLines.isEmpty() ? 0 : font.width(wrappedLines.get(lineIndex));
    }

    //TODO: Migrate this to Utilitary library
    public static int lastLineWordWrapWidth(Font font, String text, int textWidth) {
        List<String> wrappedLines = font.getSplitter().splitLines(text, textWidth, Style.EMPTY).stream().map(FormattedText::getString).toList();
        return wrappedLines.isEmpty() ? 0 : font.width(wrappedLines.getLast());
    }
}
