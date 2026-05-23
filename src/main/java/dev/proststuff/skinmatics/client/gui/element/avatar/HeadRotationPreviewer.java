package dev.proststuff.skinmatics.client.gui.element.avatar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.api.client.impl.gui.MouseEventListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public class HeadRotationPreviewer extends AbstractRenderable implements MouseEventListener {
    public static final float MAX_ROTATION = 90.0F;
    public static final int LINES = 4;

    protected float yawPercentage = 0.5F;
    protected float pitchPercentage = 0.5F;
    protected boolean isDragging = false;

    public HeadRotationPreviewer() {
        super(Skinmatics.of("head_rotation_previewer"));
    }

    public void updateValues(LivingEntity entity) {
        float halfRotation = MAX_ROTATION / 2.0F;
        entity.setYHeadRot(-halfRotation + (MAX_ROTATION * yawPercentage));
        entity.setXRot(-halfRotation + (MAX_ROTATION * pitchPercentage));
    }

    protected double normalizedX(double mouseX) {
        return roundDecimal(1.0 - Mth.clamp((mouseX - x) / getWidth(), 0.0F, 1.0F), 3);
    }

    protected double normalizedY(double mouseY) {
        return roundDecimal(Mth.clamp((mouseY - y) / getHeight(), 0.0F, 1.0F), 3);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        int width = getWidth();
        int height = getHeight();

        graphics.fill(x, y, x + getWidth(), y + getHeight(), SkinmaticsConfig.backgroundColor());
        int gridColor = SkinmaticsConfig.foregroundColor(0.25F);
        int cellWidth = width / LINES;
        int cellHeight = height / LINES;

        for (int i = 1; i < LINES; i++) {
            int lineX = x + (cellWidth * i);
            graphics.verticalLine(lineX, y - 1, y + height, gridColor);
        }

        for (int i = 1; i < LINES; i++) {
            int lineY = y + (cellHeight * i);
            graphics.horizontalLine(x, x + width - 1, lineY, gridColor);
        }

        int centerColor = SkinmaticsConfig.foregroundColor(0.5F);
        graphics.verticalLine(x + width / 2, y - 1, y + height, centerColor);
        graphics.horizontalLine(x, x + width - 1, y + height / 2, centerColor);

        int pointerX = (int) (x + (1.0F - yawPercentage) * getWidth());
        int pointerY = (int) (y + pitchPercentage * getHeight());
        graphics.fill(pointerX - 2, pointerY - 2, pointerX + 2, pointerY + 2, SkinmaticsConfig.accentColor());
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inside(event.x(), event.y())) {
            yawPercentage = (float) normalizedX(event.x());
            pitchPercentage = (float) normalizedY(event.y());

            isDragging = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (isDragging && event.button() == 0 && inside(event.x(), event.y())) {
            yawPercentage = (float) normalizedX(event.x());
            pitchPercentage = (float) normalizedY(event.y());
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (event.button() == 0 && isDragging) {
            yawPercentage = (float) normalizedX(event.x());
            pitchPercentage = (float) normalizedY(event.y());
            isDragging = false;
            return true;
        }

        return false;
    }

    @Override
    public int getWidth() {
        return 64;
    }

    @Override
    public int getHeight() {
        return 64;
    }

    public static double roundDecimal(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
}
