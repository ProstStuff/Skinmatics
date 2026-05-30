package dev.proststuff.skinmatics.client.gui.customization.avatar;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.gui.impl.ClickableRenderable;
import dev.proststuff.skinmatics.api.client.gui.skinmatics.AbstractSkinmaticsRenderable;
import dev.proststuff.skinmatics.api.client.impl.SkinmaticsMod;
import dev.proststuff.skinmatics.client.SkinmaticsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelPartHider extends AbstractSkinmaticsRenderable implements ClickableRenderable {
    protected static final Map<BodyPart, Boolean> VISIBLE = new LinkedHashMap<>();

    protected final double scale = 2;
    protected BodyPart hovered = null;
    protected BodyPart focused = null;

    public ModelPartHider() {
        super(SkinmaticsMod.ofSimple("model_part_hider"));
    }

    public boolean isFocused(BodyPart part) {
        return part == focused;
    }

    public boolean isHovered(BodyPart part) {
        return part == hovered;
    }

    public boolean isSlim() {
        Profile current = ProfileManager.getCurrent();
        return current != null ? current.slim.get() : false;
    }

    public boolean isPartVisible(BodyPart part) {
        if (part == null) return false;
        return isVisible(part);
    }

    public BodyPart getBodyPartAt(double pointX, double pointY) {
        boolean slim = isSlim();
        int torsoX = getTorsoX();
        int torsoY = getTorsoY();

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

    public int getTorsoX() {
        return getX() + (getWidth() / 2);
    }

    public int getTorsoY() {
        return getY() + (getHeight() / 2) - 6;
    }

    public int getFigureWidth() {
        return BodyPart.LEFT_ARM.getWidth(false) + BodyPart.TORSO.getWidth(false) + BodyPart.RIGHT_ARM.getWidth(false);
    }

    public int getFigureHeight() {
        return BodyPart.HEAD.height + BodyPart.TORSO.height + Math.max(BodyPart.RIGHT_LEG.height, BodyPart.LEFT_LEG.height);
    }

    @Override
    public int getWidth() {
        return (int) Math.round(32 * scale);
    }

    @Override
    public int getHeight() {
        return (int) Math.round(32 * scale);
    }

    @Override
    public boolean press(int button, double mouseX, double mouseY) {
        if (button == 0 && in(mouseX, mouseY)) {
            if (hovered != null && focused != hovered) {
                focused = hovered;
                VISIBLE.replaceAll((_, _) -> false);
                VISIBLE.put(focused, true);
            } else {
                focused = null;
                VISIBLE.replaceAll((_, _) -> true);
            }

            return true;
        }

        return false;
    }

    @Override
    public String getCategory() {
        return "modelPartHider";
    }

    public void renderPart(BodyPart part, GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean inside = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        int color = isFocused(part) ? SkinmaticsConfig.accentColor() : SkinmaticsConfig.foregroundColor();
        graphics.fill(x, y, x + width, y + height, ARGB.color((focused == null || isFocused(part)) && isPartVisible(part) ? 1.0F : 0.5F, color));

        if (inside) {
            hovered = part;
        }
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        boolean slim = isSlim();
        int torsoX = getTorsoX();
        int torsoY = getTorsoY() - (int) Math.round(BodyPart.TORSO.height / 2.0 * scale);

        hovered = null;

        for (BodyPart part : BodyPart.values()) {
            int partX = (int) Math.round(torsoX + (part.getOffsetX(slim) * scale));
            int partY = (int) Math.round(torsoY + (part.offsetY * scale));
            int width = (int) Math.round(part.getWidth(slim) * scale);
            int height = (int) Math.round(part.height * scale);

            renderPart(part, graphics, mouseX, mouseY, partX, partY, width, height);
        }

        if (hovered != null) {
            int partX = (int) Math.round(torsoX + (hovered.getOffsetX(slim) * scale));
            int partY = (int) Math.round(torsoY + (hovered.offsetY * scale));
            int width = (int) Math.round(hovered.getWidth(slim) * scale);
            int height = (int) Math.round(hovered.height * scale);

            graphics.outline(partX, partY, width, height, isFocused(hovered) ? SkinmaticsConfig.foregroundColor() : SkinmaticsConfig.accentColor());
        }
    }

    public static boolean isVisible(BodyPart part) {
        return VISIBLE.get(part);
    }

    public static void setVisibility(BodyPart part, boolean visible) {
        VISIBLE.put(part, visible);
    }
}
