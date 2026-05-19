package dev.proststuff.skinmatics.client.gui.element;

import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class SimpleButton extends AbstractRenderable implements MouseEventListener {
    public static final int SIZE = 16;

    protected final Identifier texture;

    public SimpleButton(Identifier identifier, Identifier texture) {
        super(identifier);
        this.texture = texture;
    }

    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        GraphicsUtils.sprite(graphics, texture, x, y, getWidth(), getHeight());
    }
}
