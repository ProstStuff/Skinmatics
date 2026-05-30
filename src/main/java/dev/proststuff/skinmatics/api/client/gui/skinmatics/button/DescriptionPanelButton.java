package dev.proststuff.skinmatics.api.client.gui.skinmatics.button;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class DescriptionPanelButton extends AbstractPanelButton {
    public DescriptionPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    @Override
    public int getHeight() {
        Component component = getComponent();
        Font font = GraphicsUtils.font();
        return Math.max(super.getHeight(), font.wordWrapHeight(component, AbstractPanel.WIDTH - INDENT) + (INDENT * 2));
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.textWithWordWrap(GraphicsUtils.font(), getComponent(), getX() + INDENT, getY() + (INDENT * 2) - getHeightTextIndent(), getWidth() - (INDENT * 2), foregroundColor());
    }
}
