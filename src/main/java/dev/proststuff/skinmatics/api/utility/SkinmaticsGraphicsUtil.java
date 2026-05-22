package dev.proststuff.skinmatics.api.utility;

import dev.proststuff.skinmatics.api.Profile;
import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class SkinmaticsGraphicsUtil {
    public static final int FACE_SIZE = 8;
    public static final int SKIN_SIZE = 64;
    
    public static final int FACE_POSITION = 8;
    public static final int HAT_POSITION = 40;

    public static boolean profileHead(GuiGraphicsExtractor graphics, int x, int y, Profile profile) {
        Identifier skinBase = profile.data.getSkin(false);
        Identifier skinEmissive = profile.data.getSkin(true);
        boolean rendered = skinBase != null || skinEmissive != null;
        if (skinBase != null) GraphicsUtils.sprite(graphics, skinBase, x, y, FACE_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);
        if (skinEmissive != null) GraphicsUtils.sprite(graphics, skinEmissive, x, y, FACE_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);

        if (profile.showEyes.get()) {
            int direction = EyeFeatureRenderer.EyeDirection.FRONT.ordinal();

            if (!profile.hideRightEye.get()) {
                Identifier baseEye = profile.data.getEyeLayerState(EyeFeatureRenderer.EyePosition.RIGHT).getTexture(direction);
                Identifier emissiveEye = profile.data.getEyeLayerState(EyeFeatureRenderer.EyePosition.RIGHT).getTexture(10 + direction);

                if (baseEye != null) GraphicsUtils.sprite(graphics, baseEye, x, y, FACE_SIZE, FACE_SIZE);
                if (emissiveEye != null) GraphicsUtils.sprite(graphics, emissiveEye, x, y, FACE_SIZE, FACE_SIZE);

                rendered = rendered || baseEye != null && emissiveEye != null;
            }

            if (!profile.hideLeftEye.get()) {
                Identifier baseEye = profile.data.getEyeLayerState(EyeFeatureRenderer.EyePosition.LEFT).getTexture(direction);
                Identifier emissiveEye = profile.data.getEyeLayerState(EyeFeatureRenderer.EyePosition.LEFT).getTexture(10 + direction);

                if (baseEye != null) GraphicsUtils.sprite(graphics, baseEye, x, y, FACE_SIZE, FACE_SIZE);
                if (emissiveEye != null) GraphicsUtils.sprite(graphics, emissiveEye, x, y, FACE_SIZE, FACE_SIZE);

                rendered = rendered || baseEye != null && emissiveEye != null;
            }
        }

        if (skinBase != null) GraphicsUtils.sprite(graphics, skinBase, x, y, HAT_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);
        if (skinEmissive != null) GraphicsUtils.sprite(graphics, skinEmissive, x, y, HAT_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);

        return rendered;
    }
}
