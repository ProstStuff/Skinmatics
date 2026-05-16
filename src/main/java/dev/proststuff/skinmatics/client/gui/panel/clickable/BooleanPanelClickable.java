package dev.proststuff.skinmatics.client.gui.panel.clickable;

import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.CustomizationScreen;
import dev.proststuff.utilitary.serialization.content.field.BooleanConfigField;
import dev.proststuff.utilitary.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class BooleanPanelClickable extends PanelClickable<Boolean> {
    protected boolean value;

    public BooleanPanelClickable(Identifier identifier, boolean value) {
        super(identifier);
        this.value = value;
    }

    public BooleanPanelClickable(Identifier identifier, BooleanConfigField configField) {
        super(identifier, configField);
        this.value = configField.get();
    }

    public BooleanPanelClickable(Identifier identifier) {
        this(identifier, false);
    }

    public void set(boolean value) {
        if (getField() != null) {
            getField().set(value);
            this.value = getField().get();
        } else {
            this.value = value;
        }
    }

    public void toggle() {
        set(!value);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int elementX, int elementY) {
        super.render(graphics, mouseX, mouseY, elementX, elementY);

        Component text = Component.translatable("skinmatics.clickable." + value);
        Font font = GraphicsUtils.font();
        int textWidth = font.width(text);

        int x = CustomizationScreen.SIDEBAR_SIZE + CustomizationScreen.PANEL_SIZE - 4 - textWidth;
        int y = elementY + GraphicsUtils.font().lineHeight / 2;
        int color = SkinmaticsConfig.foregroundColor();
        GraphicsUtils.text(graphics, text, 0.5F, 0.0F, x + textWidth / 2, y, color);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (inside(event.x(), event.y())) {
            toggle();
            return true;
        }

        return false;
    }
}
