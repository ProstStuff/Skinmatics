package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config.ConfigPanelButton;
import dev.proststuff.utilitary.api.config.field.value.BooleanConfigField;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class BooleanPanelButton extends ConfigPanelButton<BooleanConfigField> {
    protected final boolean literal;

    public BooleanPanelButton(SimpleIdentifier identifier, boolean literal) {
        super(identifier);
        this.literal = literal;
    }

    public Component getBooleanComponent() {
        assert field != null;
        String string = field.getAsString();

        if (literal) {
            return Component.literal(string);
        } else {
            return Component.translatable("skinmatics.value.boolean." + string);
        }
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            assert field != null;
            field.toggle();
            return true;
        }

        return false;
    }

    @Override
    public void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        GraphicsUtils.leftText(graphics, getBooleanComponent(), 0.0F, getX() + getWidth() / 2, getY() + (INDENT * 2) - getHeightTextIndent(), foregroundColor());
    }
}
