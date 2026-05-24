package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class BooleanPanelButton extends ConfigFieldPanelButton<Boolean> {
    public BooleanPanelButton(SimpleIdentifier identifier, @NonNull ConfigField<Boolean> configField) {
        super(identifier, configField);
    }

    @Override
    public int getHeight() {
        int height = GraphicsUtils.font().wordWrapHeight(Component.literal(configField.get().toString()), getWidth() / 2 - 4) + 8;
        return Math.max(super.getHeight(), height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
        Font font = GraphicsUtils.font();
        graphics.textWithWordWrap(font, getComponent(), x + getWidth(), y + 8 - font.lineHeight / 2, getWidth() / 2, SkinmaticsConfig.foregroundColor());
    }
}
