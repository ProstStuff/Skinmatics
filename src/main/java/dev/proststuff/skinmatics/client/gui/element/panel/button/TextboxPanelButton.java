package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.api.client.gui.TextboxController;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public abstract class TextboxPanelButton<V> extends ConfigFieldPanelButton<V> {
    protected TextboxController textbox = new TextboxController(Panel.WIDTH / 2);
    protected boolean previouslyFocused = false;

    public TextboxPanelButton(SimpleIdentifier identifier, ConfigField<V> configField) {
        super(identifier, configField);
    }

    public TextboxPanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> component, ConfigField<V> configField) {
        super(identifier, component, configField);
    }

    public TextboxController getTextbox() {
        return textbox;
    }

    @Override
    public int getHeight() {
        int labelHeight = super.getHeight();
        int textboxHeight = textbox.getTextHeight() + 4;
        return Math.max(16, Math.max(labelHeight, textboxHeight));
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        textbox.init(width, height);
        textbox.set(format());
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        textbox.setPosition(x + (getWidth() / 2), y + 8 - GraphicsUtils.font().lineHeight / 2);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (textbox.mouseClicked(event, doubleClick)) {
            previouslyFocused = true;
            return true;
        } else if (previouslyFocused) {
            configField.set(parse());
            previouslyFocused = false;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (textbox.keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (textbox.charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public boolean onClose() {
        configField.set(parse());
        return super.onClose();
    }

    public abstract String format();
    public abstract V parse();

    @Override
    public boolean isHovered() {
        return textbox.isFocused() || super.isHovered();
    }

    public int getTextColor() {
        return SkinmaticsConfig.foregroundColor();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
        Font font = GraphicsUtils.font();
        graphics.textWithWordWrap(font, getComponent(), x + 4, y + 8 - GraphicsUtils.font().lineHeight / 2, getWidth() / 2, SkinmaticsConfig.foregroundColor());
        textbox.render(graphics, mouseX, mouseY, tickDelta, getTextColor());
    }
}