package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.Color;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.Supplier;

public class ColorPanelButton extends TextboxPanelButton<Color> {
    public ColorPanelButton(SimpleIdentifier identifier, @NonNull ConfigField<Color> configField) {
        super(identifier, configField);
    }

    public ColorPanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> component, @NonNull ConfigField<Color> configField) {
        super(identifier, component, configField);
    }

    public int hexToInt() {
        String hex = textbox.get();
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
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (super.charTyped(event)) {
            textbox.set(textbox.get().toUpperCase(Locale.US));
        }

        return false;
    }

    @Override
    public String format() {
        return configField.get().toString().substring(2);
    }

    @Override
    public Color parse() {
        configField.get().set(hexToInt());
        return configField.get();
    }

    @Override
    public int getTextColor() {
        return hexToInt();
    }
}
