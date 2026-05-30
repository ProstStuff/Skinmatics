package dev.proststuff.skinmatics.api.client.utility;

import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.client.render.EyeRenderLayer;
import dev.proststuff.utilitary.api.utility.GraphicsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Optional;

public class SkinmaticsGraphicsUtils {
    public static final int FACE_SIZE = 8;
    public static final int SKIN_SIZE = 64;
    
    public static final int FACE_POSITION = 8;
    public static final int HAT_POSITION = 40;

    public static boolean head(GuiGraphicsExtractor graphics, int x, int y, Profile profile) {
        Identifier skinBase = profile.data.getSkin(false);
        Identifier skinEmissive = profile.data.getSkin(true);
        boolean rendered = skinBase != null || skinEmissive != null;

        if (skinBase != null) GraphicsUtils.sprite(graphics, skinBase, x, y, FACE_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);
        if (skinEmissive != null) {
            if (skinBase == null) {
                Minecraft minecraft = Minecraft.getInstance();

                try {
                    Optional<PlayerSkin> playerSkin = minecraft.getSkinManager().get(minecraft.getGameProfile()).get();
                    playerSkin.ifPresent((texture) -> GraphicsUtils.sprite(graphics, texture.body().texturePath(), x, y, FACE_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE));
                } catch (Exception _) {}
            }

            GraphicsUtils.sprite(graphics, skinEmissive, x, y, FACE_POSITION, FACE_SIZE, FACE_SIZE, FACE_SIZE, SKIN_SIZE, SKIN_SIZE);
        }

        if (profile.showEyes.get()) {
            int direction = EyeRenderLayer.EyeDirection.FRONT.ordinal();

            if (!profile.hideRightEye.get()) {
                Identifier baseEye = profile.data.getEyeLayerState(EyeRenderLayer.EyePosition.RIGHT).getTexture(direction);
                Identifier emissiveEye = profile.data.getEyeLayerState(EyeRenderLayer.EyePosition.RIGHT).getTexture(10 + direction);

                if (baseEye != null) GraphicsUtils.sprite(graphics, baseEye, x, y, FACE_SIZE, FACE_SIZE);
                if (emissiveEye != null) GraphicsUtils.sprite(graphics, emissiveEye, x, y, FACE_SIZE, FACE_SIZE);

                rendered = rendered || baseEye != null && emissiveEye != null;
            }

            if (!profile.hideLeftEye.get()) {
                Identifier baseEye = profile.data.getEyeLayerState(EyeRenderLayer.EyePosition.LEFT).getTexture(direction);
                Identifier emissiveEye = profile.data.getEyeLayerState(EyeRenderLayer.EyePosition.LEFT).getTexture(10 + direction);

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
