package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;

public class IntegerProfilePanelButton extends TextboxPanelButton<Integer>{
    public IntegerProfilePanelButton(SimpleIdentifier identifier, ConfigField<Integer> configField) {
        super(identifier, configField);
    }

    @Override
    public String format() {
        return Integer.toString(configField.get());
    }

    @Override
    public Integer parse() {
        try {
            return Integer.parseInt(textbox.get());
        } catch (NumberFormatException e) {
            return configField.get();
        }
    }
}
