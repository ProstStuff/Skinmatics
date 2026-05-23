package dev.proststuff.skinmatics.client.gui.element;

import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class SimpleButton extends AbstractRenderable implements MouseEventListener {
    protected static @Nullable SimpleButton hovered = null;
    public static final int SIZE = 16;
    protected @Nullable Identifier texture;

    public SimpleButton(SimpleIdentifier identifier, Supplier<MutableComponent> componentSupplier, @Nullable Identifier texture) {
        super(identifier, componentSupplier);
        this.texture = texture;
    }

    public SimpleButton(SimpleIdentifier identifier, @Nullable Identifier texture) {
        super(identifier);
        this.texture = texture;
    }

    public SimpleButton(SimpleIdentifier identifier) {
        this(identifier, null);
    }

    public void setTexture(@Nullable Identifier texture) {
        this.texture = texture;
    }

    public @Nullable Identifier getTexture() {
        return texture;
    }

    public void hover() {
        hovered = this;
    }

    public boolean isHovered() {
        return hovered == this;
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
    public void mouseMoved(double x, double y) {
        if (inside(x, y)) {
            hover();
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        if (getTexture() != null) {
            GraphicsUtils.sprite(graphics, getTexture(), x, y, getWidth(), getHeight());
        }
    }

    public static SimpleButton unhover() {
        SimpleButton button = hovered;
        hovered = null;
        return button;
    }
}
