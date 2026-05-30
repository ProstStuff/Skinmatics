package dev.proststuff.skinmatics.api.client.gui.impl;

import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface GuiIdentifiable {
    SimpleIdentifier getIdentifier();

    default MutableComponent getComponent() {
        return Component.translatable(getIdentifier().toLanguageKey());
    }
}
