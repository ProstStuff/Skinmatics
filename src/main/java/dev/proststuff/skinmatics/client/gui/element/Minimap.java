package dev.proststuff.skinmatics.client.gui.element;

import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import dev.proststuff.skinmatics.client.skinmatics.ProfileHandler;
import dev.proststuff.skinmatics.client.skinmatics.SkinmaticsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.Map;

public class Minimap extends AbstractRenderable implements MouseEventListener {
    protected final Map<BodyPart, Boolean> visibleParts = new EnumMap<>(BodyPart.class);
    protected double scale = 2.5;

    protected BodyPart hoveredBodyPart = null;

    public Minimap(Identifier identifier) {
        super(identifier);

        for (BodyPart bodyPart : BodyPart.values()) {
            visibleParts.put(bodyPart, true);
        }
    }

    public boolean isSlim() {
        return ProfileHandler.getCurrentProfile() != null ? ProfileHandler.getCurrentProfile().slim.get() : false;
    }

    public boolean isPartVisible(BodyPart part) {
        if (part == null) return false;
        return visibleParts.getOrDefault(part, false);
    }

    public int getFigureWidth(boolean includeInvisible) {
        boolean slim = isSlim();
        int leftArmWidth = isPartVisible(BodyPart.LEFT_ARM) || includeInvisible ? BodyPart.LEFT_ARM.getWidth(slim) : 0;
        int rightArmWidth = isPartVisible(BodyPart.RIGHT_ARM) || includeInvisible ? BodyPart.RIGHT_ARM.getWidth(slim) : 0;
        int torsoWidth = isPartVisible(BodyPart.TORSO) || includeInvisible ? BodyPart.TORSO.getWidth(slim) : 0;
        return leftArmWidth + rightArmWidth + torsoWidth;
    }

    public int getFigureHeight(boolean includeInvisible) {
        int headHeight = isPartVisible(BodyPart.HEAD) || includeInvisible ? BodyPart.HEAD.height : 0;
        int torsoHeight = isPartVisible(BodyPart.TORSO) || includeInvisible ? BodyPart.TORSO.height : 0;
        int legHeight = isPartVisible(BodyPart.RIGHT_LEG) || isPartVisible(BodyPart.LEFT_LEG) || includeInvisible ? BodyPart.RIGHT_LEG.height : 0;
        return headHeight + torsoHeight + legHeight;
    }

    public BodyPart getBodyPartAt(double pointX, double pointY) {
        boolean slim = isSlim();
        int torsoX = x + (getWidth() / 2);
        int torsoY = y + (getHeight() / 2) - 6;

        for (BodyPart part : BodyPart.values()) {
            int partX = (int) Math.round(torsoX + (part.getOffsetX(slim) * scale));
            int partY = (int) Math.round(torsoY + (part.offsetY * scale));
            int width = (int) Math.round(part.getWidth(slim) * scale);
            int height = (int) Math.round(part.height * scale);

            if (pointX >= partX && pointX < partX + width && pointY >= partY && pointY < partY + height) {
                return part;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            BodyPart bodyPart = getBodyPartAt(event.x(), event.y());
            visibleParts.put(bodyPart, !visibleParts.getOrDefault(bodyPart, false));
            return true;
        }

        return false;
    }

    public void renderPart(BodyPart part, GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean inside = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        int color = isPartVisible(part) ? SkinmaticsConfig.foregroundColor() : SkinmaticsConfig.backgroundColor();
        graphics.fill(x, y, x + width, y + height, ARGB.color(isPartVisible(part) ? 1.0F : 0.5F, color));

        if (inside) {
            hoveredBodyPart = part;
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        boolean slim = isSlim();
        int torsoX = x + (getWidth() / 2);
        int torsoY = y + (getHeight() / 2) - 6;

        hoveredBodyPart = null;
        for (BodyPart part : BodyPart.values()) {
            int partX = (int) Math.round(torsoX + (part.getOffsetX(slim) * scale));
            int partY = (int) Math.round(torsoY + (part.offsetY * scale));
            int width = (int) Math.round(part.getWidth(slim) * scale);
            int height = (int) Math.round(part.height * scale);

            renderPart(part, graphics, mouseX, mouseY, partX, partY, width, height);
        }

        if (hoveredBodyPart != null) {
            int partX = (int) Math.round(torsoX + (hoveredBodyPart.getOffsetX(slim) * scale));
            int partY = (int) Math.round(torsoY + (hoveredBodyPart.offsetY * scale));
            int width = (int) Math.round(hoveredBodyPart.getWidth(slim) * scale);
            int height = (int) Math.round(hoveredBodyPart.height * scale);

            graphics.outline(partX, partY, width, height, isPartVisible(hoveredBodyPart) ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor());
        }
    }

    @Override
    public int getWidth() {
        return (int) Math.round(32 * scale);
    }

    @Override
    public int getHeight() {
        return (int) Math.round(32 * scale);
    }

    public enum BodyPart {
        HEAD(-4, -8, 8, 8),
        TORSO(-4, 0, 8, 12),
        RIGHT_ARM(-8, -7, 0, 4, 12, 3),
        LEFT_ARM(4, 4, 0, 4, 12, 3),
        RIGHT_LEG(-4, 12, 4, 12),
        LEFT_LEG(0, 12, 4, 12);

        public final int offsetX;
        public final int slimOffsetX;
        public final int offsetY;
        public final int width;
        public final int slimWidth;
        public final int height;

        BodyPart(int offsetX, int slimOffsetX, int offsetY, int width, int height, int slimWidth) {
            this.offsetX = offsetX;
            this.slimOffsetX = slimOffsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.slimWidth = slimWidth;
            this.height = height;
        }

        BodyPart(int offsetX, int offsetY, int width, int height) {
            this(offsetX, offsetX, offsetY, width, height, width);
        }

        public int getOffsetX(boolean slim) {
            return slim ? slimOffsetX : offsetX;
        }

        public int getWidth(boolean slim) {
            return slim ? slimWidth : width;
        }
    }
}