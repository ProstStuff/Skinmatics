package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.gui.TextboxRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.NonNull;

public class TextPanelButton extends AbstractPanelButton {
    protected final TextboxRenderable textbox;

    public TextPanelButton(SimpleIdentifier identifier) {
        super(identifier);
        this.textbox = new TextboxRenderable(AbstractPanel.WIDTH - (INDENT));
    }

    public TextboxRenderable getTextbox() {
        return textbox;
    }

    public int getTextColor() {
        return foregroundColor();
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), textbox.getHeight() + INDENT);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        textbox.unfocus();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        textbox.init(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        textbox.setPosition(x + INDENT, y + (INDENT * 2) - halfLineHeight());
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
        renderClickableState(graphics);
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
