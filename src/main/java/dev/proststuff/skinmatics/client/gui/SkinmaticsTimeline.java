package dev.proststuff.skinmatics.client.gui;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.SkinmaticsTextureManager;
import dev.proststuff.skinmatics.client.skinmatics.Animatable;
import dev.proststuff.skinmatics.client.skinmatics.PersistentSkinmaticsData;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SkinmaticsTimeline {
    public int shownTicks = 0;
    public int x = -1;
    public int y = -1;
    public int width = -1;
    public int height = -1;

    public PersistentSkinmaticsData persistentSkinmaticsData;

    private final SkinmaticsTextureManager textureManager = SkinmaticsClient.TEXTURE_MANAGER;
    private final Map<Integer, SkinmaticsTextureManager.SkinmaticsTextureData> textures = new HashMap<>();

    private @Nullable Animatable animatable;

    public SkinmaticsTimeline() {}

    public Map<Integer, SkinmaticsTextureManager.SkinmaticsTextureData> getTextures() {
        return textures;
    }
    public SkinmaticsTextureManager.SkinmaticsTextureData getTexture(int tick) {
        return textures.get(tick);
    }

    public boolean setFrame(int tick, String texture) {
        if (tick > persistentSkinmaticsData.maxTicks) return false;
        SkinmaticsTextureManager.SkinmaticsTextureData skinmaticsTextureData = textureManager.getTextureData(texture);
        textures.put(tick, skinmaticsTextureData);
        return true;
    }

    public boolean setFrame(int tick, Identifier texture) {
        return setFrame(tick, texture.toString());
    }

    public void load(PersistentSkinmaticsData persistentSkinmaticsData, @Nullable Animatable animatable) {
        this.persistentSkinmaticsData = persistentSkinmaticsData;
        this.animatable = animatable;

        if (animatable != null) {
            animatable.textures().forEach((texture, ticks) -> {
                SkinmaticsTextureManager.SkinmaticsTextureData textureData = textureManager.getTextureData(texture);

                for (Integer tick : ticks) {
                    textures.put(tick, textureData);
                }
            });
        }
    }

    public void save() {
        if (animatable == null) return;

        Map<String, List<Integer>> saved = animatable.textures();
        saved.clear();
        textures.forEach((tick, texture) -> saved.computeIfAbsent(texture.texturePath(), (s) -> new ArrayList<>()).add(tick));
    }
}
