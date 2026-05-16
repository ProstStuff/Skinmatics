package dev.proststuff.skinmatics.client.gui.panel.clickable;

import dev.proststuff.utilitary.serialization.content.ConfigField;

public interface ConfigHolder<T> {
    ConfigField<T> getField();
}
