package dev.proststuff.skinmatics.client.gui.element;

import dev.proststuff.skinmatics.client.gui.impl.ScreenEventListener;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public abstract class AbstractRenderable implements ScreenEventListener {
    protected final SimpleIdentifier identifier;
    protected final Supplier<MutableComponent> componentSupplier;
    protected int screenWidth;
    protected int screenHeight;
    protected int x;
    protected int y;

    public AbstractRenderable(SimpleIdentifier identifier, Supplier<MutableComponent> component) {
        this.identifier = identifier;
        this.componentSupplier = component;
    }

    public AbstractRenderable(SimpleIdentifier identifier) {
        this(identifier, () -> Component.translatable(identifier.toLanguageKey()));
    }

    public AbstractRenderable(Identifier identifier) {
        this(SimpleIdentifier.fromIdentifier(identifier));
    }

    public SimpleIdentifier getIdentifier() {
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

    public MutableComponent getComponent() {
        return componentSupplier.get();
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta);

    public boolean inside(double pointX, double pointY) {
        return pointX >= x && pointX < x + getWidth() && pointY >= y && pointY < y + getHeight();
    }
}