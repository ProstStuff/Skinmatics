package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.impl.KeyEventListener;
import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import net.minecraft.resources.Identifier;

public abstract class PanelButton extends AbstractRenderable implements MouseEventListener, KeyEventListener {
    public PanelButton(Identifier identifier) {
        super(identifier);
    }
}
