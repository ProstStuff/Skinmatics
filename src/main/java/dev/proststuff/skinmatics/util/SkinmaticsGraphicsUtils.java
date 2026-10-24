package dev.proststuff.skinmatics.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.feature.type.Body;
import dev.proststuff.skinmatics.avatar.feature.type.eye.Eyes;
import dev.proststuff.skinmatics.avatar.texture.AvatarTextureData;
import dev.proststuff.skinmatics.avatar.manager.AvatarTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface SkinmaticsGraphicsUtils {
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

    float SCROLLING_EQUATION_THRESHOLD = (float) Math.sin((Math.PI - 1.0) / 2.0);
    float SCROLLING_EQUATION_PHASE = (float) (Math.PI / 2.0);

    static @Nullable Identifier getPlayerSkin(GameProfile profile) {
        Minecraft minecraft = Minecraft.getInstance();
        PlayerSkin playerSkin = minecraft.getSkinManager().get(profile).getNow(Optional.empty()).orElse(null);
        return playerSkin != null ? playerSkin.body().texturePath() : null;
    }


    static void head(GuiGraphicsExtractor graphics, int x, int y, float scale, AvatarProfile avatarProfile, int tickCount, String eyeState) {
        AvatarTextureData fallbackSkin = AvatarTextureData.EMPTY;

        try {
            Identifier skin = getPlayerSkin(Minecraft.getInstance().getGameProfile());
            if (skin != null) {
                fallbackSkin = AvatarTextureManager.INSTANCE.asReference(skin.toString());
            }
        } catch (Exception e) {
            Skinmatics.LOGGER.warn("Unable to get local player body texture", e);
        }

        if (avatarProfile == null) {
            if (!fallbackSkin.isEmpty()) {
                extractFace(graphics, x, y, HAT_BACK_X_POSITION, HAT_BACK_Y_POSITION, scale, HAT_SCALE, fallbackSkin, AvatarTextureData.EMPTY, true, false);
                extractFace(graphics, x, y, HEAD_X_POSITION, HEAD_Y_POSITION, scale, HEAD_SCALE, fallbackSkin, AvatarTextureData.EMPTY, false, false);
                extractFace(graphics, x, y, HAT_FRONT_X_POSITION, HAT_FRONT_Y_POSITION, scale, HAT_SCALE, fallbackSkin, AvatarTextureData.EMPTY, false, false);
            }

            return;
        }

        int maxTicks = avatarProfile.maxTicks();
        AvatarFeatureData<Body> featureData = avatarProfile.getFeature(AvatarFeatureTypes.BODY);
        AvatarTextureData skin, emissiveSkin;

        if (featureData != null) {
            skin = featureData.settings().baseTextures().get(tickCount, maxTicks);
            emissiveSkin = featureData.settings().emissiveTextures().get(tickCount, maxTicks);
        } else {
            skin = AvatarTextureData.EMPTY;
            emissiveSkin = AvatarTextureData.EMPTY;
        }

        if (skin.isEmpty()) {
            skin = fallbackSkin;
        }

        extractFace(graphics, x, y, HAT_BACK_X_POSITION, HAT_BACK_Y_POSITION, scale, HAT_SCALE, skin, emissiveSkin, true, false);
        extractFace(graphics, x, y, HEAD_X_POSITION, HEAD_Y_POSITION, scale, HEAD_SCALE, skin, emissiveSkin, false, false);
        AvatarFeatureData<Eyes> eyes = avatarProfile.getFeature(AvatarFeatureTypes.EYES);
        if (eyes != null) extractEyes(graphics, x, y, scale, maxTicks, eyes.settings(), tickCount, eyeState);
        extractFace(graphics, x, y, HAT_FRONT_X_POSITION, HAT_FRONT_Y_POSITION, scale, HAT_SCALE, skin, emissiveSkin, false, false);
    }

    static void extractFace(GuiGraphicsExtractor graphics, int x, int y, int uvX, int uvY, float scale, float scaleMultiplier, AvatarTextureData skin, AvatarTextureData emissiveSkin, boolean mirrorX, boolean mirrorY) {
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

    static void extractEyes(GuiGraphicsExtractor graphics, int x, int y, float scale, int maxTicks, Eyes eyes, int tickCount, String eyeState) {
        int size = Math.round(FACE_SIZE * scale);

        for (Map.Entry<String, Eyes.EyeTextureSet> entry : eyes.baseTextures().entrySet()) {
            String group = entry.getKey();
            Eyes.EyeTextureSet textureSet = entry.getValue();

            AvatarTextureData texture = textureSet.get(eyeState).get(tickCount, maxTicks);

            if (!texture.isEmpty()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, texture.id(), x, y, 0, 0, size, size, size, size, size, size);
            }

            AvatarTextureData emissiveTexture = eyes.emissiveTextures().getOrDefault(group, Eyes.EyeTextureSet.EMPTY).get(eyeState).get(tickCount, maxTicks);
            if (!emissiveTexture.isEmpty()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, emissiveTexture.id(), x, y, 0, 0, size, size, size, size, size, size);
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

    static void extractScrollingText(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int width, int color, double nanoTime) {
        int textWidth = font.width(text);

        if (textWidth > width) {
            // Surely there's a more optimized equation right??? This thing scares me...
            float alpha = (float) ((1.0 + Math.clamp(Math.sin((nanoTime / 1000000000L) - SCROLLING_EQUATION_PHASE), -SCROLLING_EQUATION_THRESHOLD, SCROLLING_EQUATION_THRESHOLD) / SCROLLING_EQUATION_THRESHOLD) * 0.5);
            float offset = alpha * (textWidth - width);

            graphics.enableScissor(x, y, x + width, y + font.lineHeight);
            graphics.text(font, text, (int) (x - offset), y, color);
            graphics.disableScissor();
        } else {
            graphics.text(font, text, x, y, color);
        }
    }
}