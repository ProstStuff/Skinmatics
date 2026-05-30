package dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config;

import dev.proststuff.utilitary.api.config.field.value.StringConfigField;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;

public class StringTextConfigPanelButton extends TextConfigPanelButton<String, StringConfigField> {
    public StringTextConfigPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    @Override
    public String parse(String value) {
        return value;
    }
}
