package dev.proststuff.skinmatics.client.gui.element.panel.button;

import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public abstract class ConfigFieldPanelButton<V> extends PanelButton {
    protected final @NonNull ConfigField<V> configField;

    public ConfigFieldPanelButton(SimpleIdentifier identifier, @NonNull ConfigField<V> configField) {
        super(identifier);
        this.configField = configField;
    }

    public ConfigFieldPanelButton(SimpleIdentifier identifier, Supplier<MutableComponent> component, @NonNull ConfigField<V> configField) {
        super(identifier, component);
        this.configField = configField;
    }

    @Override
    public int getHeight() {
        Font font = GraphicsUtils.font();
        int height = font.wordWrapHeight(getComponent(), getWidth() / 2 - 4) + 8;
        return Math.max(super.getHeight(), height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
        renderConfigName(graphics);
    }

    public void renderConfigName(GuiGraphicsExtractor graphics) {
        Font font = GraphicsUtils.font();
        graphics.textWithWordWrap(font, getComponent(), x + 4, y + 8 - GraphicsUtils.font().lineHeight / 2, getWidth() / 2, SkinmaticsConfig.foregroundColor());
    }
}
