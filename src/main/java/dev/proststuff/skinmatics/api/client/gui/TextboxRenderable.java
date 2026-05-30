package dev.proststuff.skinmatics.api.client.gui;

import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class TextboxRenderable extends AbstractRenderable implements ClickableRenderable {
    protected int width;
    protected @NonNull Supplier<Component> placeholder = Component::empty;
    protected @NonNull Supplier<Component> unit = Component::empty;
    protected @NonNull String content = "";
    protected int cursor = 0;
    protected boolean editable = true;

    protected CharacterVerification verification = Character::isISOControl;
    protected CheckTextModification modification = (text, textBeforeModification, codepointCharacter, cursor) -> text;
    protected RenderTextFormatter formatter = Component::literal;

    public TextboxRenderable(int width) {
        this.width = width;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setVerification(CharacterVerification verification) {
        this.verification = verification;
    }

    public void setModification(CheckTextModification modification) {
        this.modification = modification;
    }

    public void setPlaceholder(@NonNull Supplier<Component> placeholder) {
        this.placeholder = placeholder;
    }

    public void setPlaceholder(@NonNull Component placeholder) {
        setPlaceholder(() -> placeholder);
    }

    public void setUnit(@NonNull Supplier<Component> unit) {
        this.unit = unit;
    }

    public void setUnit(@NonNull Component unit) {
        setUnit(() -> unit);
    }

    public boolean isEditable() {
        return editable;
    }

    public int getCursor() {
        return cursor;
    }

    public @NonNull String getContent() {
        return content;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public void setContent(String content) {
        this.content = modification.modified(content, this.content, -1, cursor);
        this.cursor = Math.max(this.cursor, this.content.length());
    }
    
    public Font font() {
        return GraphicsUtils.font();
    }

    public int getFullWidth() {
        return width;
    }

    @Override
    public int getWidth() {
        return width - getUnitWidth();
    }

    public int getUnitWidth() {
        return font().width(unit.get());
    }

    @Override
    public int getHeight() {
        return Math.max(font().wordWrapHeight(formatter.format(content), getWidth()), font().lineHeight);
    }

    @Override
    public void focus() {
        ClickableRenderable.super.focus();
        this.cursor = this.content.length();
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        return isEditable() && !isFocused();
    }

    @Override
    public String getCategory() {
        return "textbox";
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        render(graphics, 0xFF000000);
    }

    public void render(@NonNull GuiGraphicsExtractor graphics, int textColor, int placeholderColor, int unitColor) {
        Font font = font();
        int drawY = renderMultiline(graphics, textColor);

        if (content.isEmpty()) {
            if (isFocused()) {
                boolean visible = ((System.currentTimeMillis() / 500) % 2 == 0);
                if (visible) {
                    graphics.fill(getX(), drawY, getX() + 1, drawY + font.lineHeight, textColor);
                }
            } else {
                renderPlaceholder(graphics, placeholderColor);
            }
        }

        renderUnit(graphics, unitColor);
    }

    public void render(@NonNull GuiGraphicsExtractor graphics, int primaryColor, int secondaryColor) {
        render(graphics, primaryColor, secondaryColor, secondaryColor);
    }

    public void render(@NonNull GuiGraphicsExtractor graphics, int color) {
        render(graphics, color, color);
    }

    public int renderMultiline(GuiGraphicsExtractor graphics, int color) {
        Font font = font();
        int drawY = getY();
        int drawX = getX();

        List<String> lines = font.getSplitter().splitLines(formatter.format(content), getWidth(), Style.EMPTY).stream().map(FormattedText::getString).toList();
        int remainingCursor = getCursor();

        for (String line : lines) {
            graphics.text(font, line, drawX, drawY, color);

            if (isFocused() && remainingCursor >= 0) {
                boolean cursorInThisLine = remainingCursor <= line.length();

                if (cursorInThisLine) {
                    boolean visible = ((System.currentTimeMillis() / 500) % 2 == 0);

                    if (visible) {
                        String beforeCursor = line.substring(0, remainingCursor);
                        int cursorX = drawX + font.width(beforeCursor);

                        graphics.fill(cursorX, drawY, cursorX + 1, drawY + font.lineHeight, color);
                    }

                    remainingCursor = -1;
                } else {
                    remainingCursor -= line.length();
                }
            }

            drawY += font.lineHeight;
        }

        return drawY;
    }

    public void renderUnit(GuiGraphicsExtractor graphics, int color) {
        color = ARGB.setBrightness(color, 0.75F);
        GraphicsUtils.leftText(graphics, unit.get(), 0.5F, getX() + getWidth(), getY() + getHeight() / 2, color);
    }

    public void renderPlaceholder(GuiGraphicsExtractor graphics, int color) {
        color = ARGB.multiplyAlpha(color, 0.5F);
        GraphicsUtils.leftText(graphics, placeholder.get(), 0.0F, getX(), getY(), color);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!(isFocused() && editable)) return false;
        int key = event.key();

        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (cursor > 0) {
                    content = modification.modified(content.substring(0, cursor - 1) + content.substring(cursor), content, -1, cursor);
                    cursor--;
                }

                return true;
            }

            case GLFW.GLFW_KEY_DELETE -> {
                if (cursor < content.length()) {
                    content = modification.modified(content.substring(0, cursor) + content.substring(cursor + 1), content, -1, cursor);
                }
                return true;
            }

            case GLFW.GLFW_KEY_LEFT -> {
                cursor = Math.max(0, cursor - 1);
                return true;
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                cursor = Math.min(content.length(), cursor + 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (!(isFocused() && editable)) return false;
        int c = event.codepoint();

        if (verification.verify(c)) {
            content = modification.modified(content.substring(0, cursor) + Character.toString(c) + content.substring(cursor), content, c, cursor);
            cursor++;
            return true;
        }

        return false;
    }

    public interface CharacterVerification {
        boolean verify(int codepoint);
    }

    public interface CheckTextModification {
        String modified(String text, String textBeforeModification, int codepointCharacter, int cursor);
    }

    public interface RenderTextFormatter {
        Component format(String text);
    }
}