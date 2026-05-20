package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.skinmatics.client.gui.impl.KeyEventListener;
import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public abstract class PanelButton extends AbstractRenderable implements MouseEventListener, KeyEventListener {
    public PanelButton(Identifier identifier) {
        super(identifier);
    }

    public PanelButton(Identifier identifier, Supplier<Component> component) {
        super(identifier, component);
    }

    @Override
    public int getWidth() {
        return Panel.WIDTH;
    }
}
