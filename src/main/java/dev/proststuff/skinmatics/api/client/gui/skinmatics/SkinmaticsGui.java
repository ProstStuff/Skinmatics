package dev.proststuff.skinmatics.api.client.gui.skinmatics;

import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsClientMod;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface SkinmaticsGui extends SkinmaticsClientMod {
    default Font font() {
        return GraphicsUtils.font();
    }

    default int lineHeight() {
        return font().lineHeight;
    }

    default int halfLineHeight() {
        return lineHeight() / 2;
    }

    default int backgroundColor() {
        return config().backgroundColor.get(0.5F);
    }

    default int backgroundColor(float alpha) {
        return config().backgroundColor.get(alpha);
    }

    default int foregroundColor() {
        return config().foregroundColor.get(1.0F);
    }

    default int foregroundColor(float alpha) {
        return config().foregroundColor.get(alpha);
    }

    default int accentColor() {
        return config().accentColor.get(1.0F);
    }

    default int accentColor(float alpha) {
        return config().accentColor.get(alpha);
    }

    default void renderClickableState(GuiGraphicsExtractor graphics) {
        if (this instanceof ClickableRenderable clickableRenderable) {
            clickableRenderable.renderClickableState(graphics, accentColor(), foregroundColor(0.5F));
        }
    }
}
