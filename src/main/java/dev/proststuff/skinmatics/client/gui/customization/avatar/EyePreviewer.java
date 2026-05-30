package dev.proststuff.skinmatics.client.gui.customization.avatar;

import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractSkinmaticsRenderable;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public class EyePreviewer extends AbstractSkinmaticsRenderable implements ClickableRenderable {
    public static final float MAX_ROTATION = 90.0F;
    public static final int LINES = 4;

    protected float yawPercentage = 0.5F;
    protected float pitchPercentage = 0.5F;
    protected boolean isDragging = false;

    public EyePreviewer() {
        super(SkinmaticsMod.ofSimple("eyes"));
    }

    public void update(LivingEntity entity) {
        float halfRotation = MAX_ROTATION / 2.0F;
        entity.setYHeadRot(-halfRotation + (MAX_ROTATION * yawPercentage));
        entity.setXRot(-halfRotation + (MAX_ROTATION * pitchPercentage));
    }

    protected double normalizedX(double mouseX) {
        return roundDecimal(1.0 - Mth.clamp((mouseX - getX()) / getWidth(), 0.0F, 1.0F), 3);
    }

    protected double normalizedY(double mouseY) {
        return roundDecimal(Mth.clamp((mouseY - getY()) / getHeight(), 0.0F, 1.0F), 3);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), SkinmaticsConfig.backgroundColor());
        int gridColor = SkinmaticsConfig.foregroundColor(0.25F);
        int cellWidth = getWidth() / LINES;
        int cellHeight = getHeight() / LINES;

        for (int i = 1; i < LINES; i++) {
            int lineX = getX() + (cellWidth * i);
            graphics.verticalLine(lineX, getY() - 1, getY() + getHeight(), gridColor);
        }

        for (int i = 1; i < LINES; i++) {
            int lineY = getY() + (cellHeight * i);
            graphics.horizontalLine(getX(), getX() + getWidth() - 1, lineY, gridColor);
        }

        int centerColor = SkinmaticsConfig.foregroundColor(0.5F);
        graphics.verticalLine(getX() + getWidth() / 2, getY() - 1, getY() + getHeight(), centerColor);
        graphics.horizontalLine(getX(), getX() + getWidth() - 1, getY() + getHeight() / 2, centerColor);

        int pointerX = (int) (getX() + (1.0F - yawPercentage) * getWidth());
        int pointerY = (int) (getY() + pitchPercentage * getHeight());
        graphics.fill(pointerX - 2, pointerY - 2, pointerX + 2, pointerY + 2, SkinmaticsConfig.accentColor());
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            yawPercentage = (float) normalizedX(mouseX);
            pitchPercentage = (float) normalizedY(mouseY);

            isDragging = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (isDragging && event.button() == 0 && in(event.x(), event.y())) {
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

    @Override
    public String getCategory() {
        return "eyes";
    }

    public static double roundDecimal(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
}