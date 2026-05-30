package dev.proststuff.skinmatics.api.client.gui.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.AbstractRenderable;
import dev.proststuff.skinmatics.api.client.gui.impl.GuiIdentifiable;
import dev.proststuff.skinmatics.client.gui.customization.SkinmaticsCustomizationScreen;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractSkinmaticsRenderable extends AbstractRenderable<SkinmaticsCustomizationScreen> implements GuiIdentifiable, SkinmaticsGui {
    protected final SimpleIdentifier identifier;

    public AbstractSkinmaticsRenderable(SimpleIdentifier identifier) {
        this.identifier = identifier;
    }

    public void renderBackground(GuiGraphicsExtractor graphics) {
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), backgroundColor());
    }

    @Override
    public SimpleIdentifier getIdentifier() {
        return identifier;
    }
}
