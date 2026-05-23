package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;

public class StringPanelButton extends TextboxPanelButton<String> {
    public StringPanelButton(SimpleIdentifier identifier, ConfigField<String> configField) {
        super(identifier, configField);
    }

    @Override
    public String format() {
        return configField.get();
    }

    @Override
    public String parse() {
        return textbox.get();
    }
}
