package dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config;

import dev.proststuff.skinmatics.api.client.gui.impl.ConfigHolder;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.AbstractPanelButton;
import dev.proststuff.utilitary.api.config.field.ConfigField;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class ConfigPanelButton<F extends ConfigField<?>> extends AbstractPanelButton implements ConfigHolder<F> {
    protected @Nullable F field;

    public ConfigPanelButton(SimpleIdentifier identifier) {
        super(identifier);
    }

    public void setField(@Nullable F field) {
        this.field = field;
        init(getScreenWidth(), getScreenHeight());
    }

    public boolean isDirty() {
        return field != null && field.isDirty();
    }

    public void reset() {
        assert field != null;
        field.setDefault();
    }

    @Override
    public @Nullable F getField() {
        return field;
    }

    @Override
    public int getHeight() {
        Font font = GraphicsUtils.font();
        int height = font.wordWrapHeight(getComponent(), getWidth() / 2 - INDENT) + (INDENT * 2);
        return Math.max(super.getHeight(), height);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        renderClickableState(graphics);

        if (field != null) {
            renderField(graphics);
            renderContent(graphics, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1 && in(event.x(), event.y())) {
            reset();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    public void renderField(GuiGraphicsExtractor graphics) {
        Font font = GraphicsUtils.font();
        graphics.textWithWordWrap(font, getComponent(), getX() + INDENT, getY() + (INDENT * 2) - halfLineHeight(), getWidth() / 2, foregroundColor());
    }

    public abstract void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta);
}
