package dev.proststuff.skinmatics.api.client.gui.impl;

import dev.proststuff.utilitary.api.config.field.ConfigField;

public interface ConfigHolder<F extends ConfigField<?>> {
    F getField();
}
