package dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config;

import dev.proststuff.skinmatics.api.client.gui.TextboxRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.NonNull;

public abstract class TextConfigPanelButton<V, F extends ConfigField<V>> extends ConfigPanelButton<F> {
    protected final TextboxRenderable textbox;
    protected boolean initialized = false;

    public TextConfigPanelButton(SimpleIdentifier identifier) {
        super(identifier);
        this.textbox = new TextboxRenderable(AbstractPanel.WIDTH / 2 - INDENT);
    }

    public TextboxRenderable getTextbox() {
        return textbox;
    }

    public int getTextColor() {
        return foregroundColor();
    }

    public String format() {
        assert field != null;
        return field.getAsString();
    }

    public abstract V parse(String value);

    @Override
    public void reset() {
        super.reset();
        textbox.setContent(format());
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), textbox.getHeight() + INDENT);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        textbox.unfocus();

        if (initialized && field != null) {
            field.set(parse(textbox.getContent()));
        }
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        textbox.init(width, height);
        initialized = false;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        textbox.setPosition(x + getWidth() / 2, y + (INDENT * 2) - halfLineHeight());
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            if (isFocused()) {
                if (textbox.isFocused()) {
                    textbox.unfocus();
                } else {
                    textbox.focus();
                }

                return true;
            }

            return !isFocused();
        }

        return false;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (!initialized && field != null) {
            textbox.setContent(format());
            initialized = true;
        }

        super.render(graphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        textbox.render(graphics, getTextColor());
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        return textbox.keyPressed(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        return textbox.charTyped(event);
    }
}