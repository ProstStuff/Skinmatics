package dev.proststuff.skinmatics.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.data.Eyes;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.skinmatics.texture.TextureData;
import dev.proststuff.utilitary.Utilitary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Map;
import java.util.Optional;

public interface SkinmaticsRenderUtils {
    int FACE_SIZE = 8;
    int SKIN_SIZE = 64;

    int HEAD_X_POSITION = 8;
    int HEAD_Y_POSITION = 8;
    int HAT_BACK_X_POSITION = 56;
    int HAT_BACK_Y_POSITION = 8;
    int HAT_FRONT_X_POSITION = 40;
    int HAT_FRONT_Y_POSITION = 8;

    float HEAD_SCALE = 1.0F;
    float HAT_SCALE = 1.125F;

    static void head(GuiGraphicsExtractor graphics, int x, int y, float scale, Profile profile, int tickCount, String eyeState) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerSkin playerSkin = null;
        TextureData fallbackSkin = TextureData.EMPTY;

        try {
            playerSkin = minecraft.getSkinManager().get(minecraft.getGameProfile()).getNow(Optional.empty()).orElse(null);
            if (playerSkin != null) fallbackSkin = SkinmaticsTextureManager.getOrLoad(playerSkin.body().texturePath().toString());
        } catch (Exception e) {
            if (Utilitary.CONFIG.debugLogging()) SkinmaticsMod.LOGGER.warn("Unable to getLoaded local player skin texture");
        }

        if (profile == null) {
            if (playerSkin != null) {
                renderFace(graphics, x, y, HAT_BACK_X_POSITION, HAT_BACK_Y_POSITION, scale, HAT_SCALE, fallbackSkin, TextureData.EMPTY, true, false);
                renderFace(graphics, x, y, HEAD_X_POSITION, HEAD_Y_POSITION, scale, HEAD_SCALE, fallbackSkin, TextureData.EMPTY, false, false);
                renderFace(graphics, x, y, HAT_FRONT_X_POSITION, HAT_FRONT_Y_POSITION, scale, HAT_SCALE, fallbackSkin, TextureData.EMPTY, false, false);
            }

            return;
        }

        TextureData skin = profile.skin().getTextures().get(tickCount, profile.maxTicks());
        TextureData emissiveSkin = profile.skin().getEmissiveTextures().get(tickCount, profile.maxTicks());

        if (skin.isEmpty()) skin = fallbackSkin;

        renderFace(graphics, x, y, HAT_BACK_X_POSITION, HAT_BACK_Y_POSITION, scale, HAT_SCALE, skin, emissiveSkin, true, false);
        renderFace(graphics, x, y, HEAD_X_POSITION, HEAD_Y_POSITION, scale, HEAD_SCALE, skin, emissiveSkin, false, false);
        renderEyes(graphics, x, y, scale, profile, tickCount, eyeState);
        renderFace(graphics, x, y, HAT_FRONT_X_POSITION, HAT_FRONT_Y_POSITION, scale, HAT_SCALE, skin, emissiveSkin, false, false);
    }

    static void renderFace(GuiGraphicsExtractor graphics, int x, int y, int uvX, int uvY, float scale, float scaleMultiplier, TextureData skin, TextureData emissiveSkin, boolean mirrorX, boolean mirrorY) {
        float scale0 = scale;
        scale *= scaleMultiplier;

        int scaledUVX = Math.round(uvX * scale);
        int scaledUVY = Math.round(uvY * scale);
        int faceSize = Math.round(FACE_SIZE * scale);
        int skinSize = Math.round(SKIN_SIZE * scale);

        int size0 = Math.round(FACE_SIZE * scale0);
        int offset = (faceSize - size0) / 2;

        blitMirrored(graphics, RenderPipelines.GUI_TEXTURED, skin.id(), x - offset, y - offset, scaledUVX, scaledUVY, faceSize, faceSize, skinSize, skinSize, -1, mirrorX, mirrorY);

        if (!emissiveSkin.isEmpty()) {
            blitMirrored(graphics, RenderPipelines.GUI_TEXTURED, emissiveSkin.id(), x - offset, y - offset, scaledUVX, scaledUVY, faceSize, faceSize, skinSize, skinSize, -1, mirrorX, mirrorY);
        }
    }

    static void renderEyes(GuiGraphicsExtractor graphics, int x, int y, float scale, Profile profile, int tickCount, String eyeState) {
        int size = Math.round(FACE_SIZE * scale);

        for (Map.Entry<String, Eyes.EyeTextureSet> entry : profile.eyes().textures().entrySet()) {
            String group = entry.getKey();
            Eyes.EyeTextureSet textureSet = entry.getValue();

            TextureData texture = textureSet.get(eyeState).get(tickCount, profile.maxTicks());

            if (!texture.isEmpty()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, texture.id(), x, y, 0, 0, size, size, size, size, size, -1);
            }

            TextureData emissiveTexture = profile.eyes().emissiveTextures().getOrDefault(group, Eyes.EyeTextureSet.EMPTY).get(eyeState).get(tickCount, profile.maxTicks());
            if (!emissiveTexture.isEmpty()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, emissiveTexture.id(), x, y, 0, 0, size, size, size, size, size, -1);
            }
        }
    }

    static void blitMirrored(
            GuiGraphicsExtractor graphics,
            RenderPipeline renderPipeline,
            Identifier texture,
            int x, int y, float u, float v,
            int width, int height, int srcWidth, int srcHeight,
            int textureWidth, int textureHeight,
            int color, boolean mirrorX, boolean mirrorY
    ) {
        float u0 = u / textureWidth;
        float u1 = (u + srcWidth) / textureWidth;
        float v0 = v / textureHeight;
        float v1 = (v + srcHeight) / textureHeight;

        if (mirrorX) {
            float t = u0;
            u0 = u1;
            u1 = t;
        }

        if (mirrorY) {
            float t = v0;
            v0 = v1;
            v1 = t;
        }

        graphics.innerBlit(renderPipeline, texture, x, x + width, y, y + height, u0, u1, v0, v1, color);
    }

    static void blitMirrored(
            GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Identifier texture,
            int x, int y, float u, float v,
            int width, int height, int textureWidth, int textureHeight,
            int color, boolean mirrorX, boolean mirrorY
    ) {
        blitMirrored(graphics, renderPipeline, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color, mirrorX, mirrorY);
    }
}
