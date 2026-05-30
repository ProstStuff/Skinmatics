package dev.proststuff.skinmatics.client.gui.customization.panel;

import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractPanel;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.AbstractPanelButton;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.button.DescriptionPanelButton;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public class TimelinePanel extends AbstractPanel {
    public TimelinePanel() {
        super(SkinmaticsMod.ofSimple("timeline"), true);

        for (int i = 0; i < 4; i++) {
            addButton(new DescriptionPanelButton(SkinmaticsMod.ofSimple(Integer.toString(i))));
        }
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
    }

    @Override
    public void renderButtons(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta, int startY) {
        int viewportLeft = getX();
        int viewportRight = getX() + getWidth();

        graphics.enableScissor(viewportLeft, getY(), viewportRight, getY() + getHeight());

        int currentX = getX() - (int) scrollingPosition;
        int currentY = getY() + startY;

        for (AbstractPanelButton button : buttons) {
            int buttonLeft = currentX;
            int buttonRight = currentX + button.getWidth();
            boolean visible = buttonRight >= viewportLeft && buttonLeft <= viewportRight;

            if (visible) {
                button.setPosition(currentX, currentY);
                button.render(graphics, mouseX, mouseY, tickDelta);
            }

            currentX += button.getWidth();
            currentY += button.getHeight();
        }

        graphics.disableScissor();
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.render(graphics, mouseX, mouseY, tickDelta);
    }
}
