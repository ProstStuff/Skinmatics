package dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config;

import dev.proststuff.utilitary.api.config.field.value.utilitary.ColorConfigField;
import dev.proststuff.utilitary.api.utility.Color;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class ColorTextConfigPanelButton extends TextConfigPanelButton<Color, ColorConfigField> {
    public ColorTextConfigPanelButton(SimpleIdentifier identifier) {
        super(identifier);

        textbox.setVerification((codepoint -> Character.digit(codepoint, 16) != -1));
        textbox.setModification((text, oldText, _, _) -> text.length() <= 6 ? text : oldText);

        textbox.setPlaceholder(Component.literal("FFFFFF"));
    }

    public int hexToInt(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() == 6) {
            hex = "FF" + hex;
        } else if (hex.length() == 8 && !hex.startsWith("FF")) {
            hex = "FF" + hex.substring(2);
        }

        try {
            int color = (int) Long.parseLong(hex, 16);
            return ARGB.alphaFloat(color) != 1.0F ? 0xFFFFFFFF : color;
        } catch (NumberFormatException _) {
            return 0xFFFFFFFF;
        }
    }

    @Override
    public String format() {
        assert field != null;
        return field.get().toString().substring(2);
    }

    @Override
    public Color parse(String value) {
        assert field != null;
        field.set(hexToInt(value));
        return field.get();
    }

    @Override
    public int getTextColor() {
        if (isFocused()) {
            return (System.currentTimeMillis() / 500) % 2 == 0 ? foregroundColor() : hexToInt(textbox.getContent());
        } else if (isHovered()) {
            return hexToInt(textbox.getContent());
        }

        return foregroundColor();
    }
}
