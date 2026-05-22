package dev.proststuff.skinmatics.client.gui.element.avatar;

import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.client.gui.element.AbstractRenderable;
import dev.proststuff.skinmatics.client.gui.impl.MouseEventListener;
import dev.proststuff.skinmatics.api.ProfileHandler;
import dev.proststuff.skinmatics.api.SkinmaticsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.Map;

public class AvatarMinimap extends AbstractRenderable implements MouseEventListener {
    public static final Map<BodyPart, Boolean> VISIBLE = new EnumMap<>(BodyPart.class);
    protected double scale = 2;

    static {
        for (BodyPart bodyPart : BodyPart.values()) {
            VISIBLE.put(bodyPart, true);
        }
    }

    protected BodyPart focusedBodyPart = null;
    protected BodyPart hoveredBodyPart = null;

    public AvatarMinimap() {
        super(Skinmatics.of("minimap"));
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        Minecraft minecraft = Minecraft.getInstance();

        for (BodyPart part : BodyPart.values()) {
            if (minecraft.player == null) {
                VISIBLE.put(part, false);
            } else {
                VISIBLE.put(part, true);
            }
        }
    }

    public void setPartVisibility(BodyPart part, boolean visible) {
        setVisibility(part, visible);
    }

    public boolean isSlim() {
        return ProfileHandler.getCurrentProfile() != null ? ProfileHandler.getCurrentProfile().slim.get() : false;
    }

    public boolean isFocused(BodyPart part) {
        return part == focusedBodyPart;
    }

    public boolean isPartVisible(BodyPart part) {
        if (part == null) return false;
        return isVisible(part);
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
        if (event.button() == 0 && inside(event.x(), event.y())) {
            BodyPart part = getBodyPartAt(event.x(), event.y());
            focusedBodyPart = part != null ? (focusedBodyPart == part ? null : part) : focusedBodyPart;

            for (BodyPart bodyPart : BodyPart.values()) {
                if (focusedBodyPart == null || bodyPart == focusedBodyPart) {
                    VISIBLE.put(bodyPart, true);
                } else {
                    VISIBLE.put(bodyPart, false);
                }
            }
            return true;
        }

        return false;
    }

    public void renderPart(BodyPart part, GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean inside = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        int color = isFocused(part) ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor();
        graphics.fill(x, y, x + width, y + height, ARGB.color((focusedBodyPart == null || isFocused(part)) && isPartVisible(part) ? 1.0F : 0.5F, color));

        if (inside) {
            hoveredBodyPart = part;
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        boolean slim = isSlim();
        int torsoX = x + (getWidth() / 2);
        int torsoY = y + (getHeight() / 2) - (int) Math.round(BodyPart.TORSO.height / 2.0 * scale);

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

            graphics.outline(partX, partY, width, height, isFocused(hoveredBodyPart) ? SkinmaticsConfig.foregroundColor() : SkinmaticsConfig.accentColor());
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

    public static boolean isVisible(BodyPart part) {
        return VISIBLE.get(part);
    }

    public static void setVisibility(BodyPart part, boolean visible) {
        VISIBLE.put(part, visible);
    }
}