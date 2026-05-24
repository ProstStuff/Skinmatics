package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.panel.Panel;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class DescriptionPanelButton extends PanelButton {
    public DescriptionPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    public DescriptionPanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> componentSupplier) {
        super(identifier, componentSupplier);
    }

    @Override
    public int getHeight() {
        Component component = getComponent();
        Font font = GraphicsUtils.font();
        return Math.max(16, font.wordWrapHeight(component, Panel.WIDTH - 8) + 8);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.textWithWordWrap(GraphicsUtils.font(), getComponent(), x + 4, y + 8 - GraphicsUtils.font().lineHeight / 2, getWidth() - 8, SkinmaticsConfig.foregroundColor());
    }
}
