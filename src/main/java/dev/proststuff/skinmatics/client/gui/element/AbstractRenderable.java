package dev.proststuff.skinmatics.client.gui.element;

import dev.proststuff.skinmatics.client.gui.impl.ScreenEventListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public abstract class AbstractRenderable implements ScreenEventListener {
    protected final Identifier identifier;
    protected int screenWidth;
    protected int screenHeight;
    protected int x;
    protected int y;

    public AbstractRenderable(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void init(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Component getComponent() {
        return Component.translatable(identifier.getNamespace() + "." + identifier.getPath());
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta);

    public boolean inside(double pointX, double pointY) {
        return pointX >= x && pointX < x + getWidth() && pointY >= y && pointY < y + getHeight();
    }
}