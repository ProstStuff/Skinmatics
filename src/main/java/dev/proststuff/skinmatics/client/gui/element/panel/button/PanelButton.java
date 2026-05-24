package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.api.client.impl.gui.KeyEventListener;
import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.SimpleButton;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public abstract class PanelButton extends SimpleButton implements MouseEventListener, KeyEventListener {
    public PanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    public PanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> component) {
        super(identifier, component, null);
    }

    @Override
    public int getWidth() {
        return Panel.WIDTH;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderBackground(graphics, SkinmaticsConfig.foregroundColor(0.5F));
    }

    public void renderBackground(GuiGraphicsExtractor graphics, int color) {
        if (isHovered()) {
            graphics.fill(x, y, x + getWidth(), y + getHeight(), color);
        }
    }
}
