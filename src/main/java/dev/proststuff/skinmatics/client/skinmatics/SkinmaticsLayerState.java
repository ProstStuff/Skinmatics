package dev.proststuff.skinmatics.client.skinmatics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.TreeMap;

@Environment(EnvType.CLIENT)
public class SkinmaticsLayerState {
    public TreeMap<Integer, Identifier>[] textures;
    public Identifier currentTexture;
    public int ticks = 0;
    public int nextTicks = -1;
    public int state = 0;
    public boolean emissive = false;

    public SkinmaticsLayerState(int size) {
        textures = new TreeMap[size];

        for (int i = 0; i < textures.length; i++) {
            textures[i] = new TreeMap<>();
        }
    }

    public RenderLayer getRenderLayer(Identifier fallback, SkinmaticsData skinmaticsData) {
        Identifier texture = get();
        if (texture == null) texture = fallback;

        return emissive ? skinmaticsData.getEmissiveRenderLayer(texture) : RenderLayers.entityTranslucent(texture);
    }

    public TreeMap<Integer, Identifier> getState(int state) {
        return textures[state];
    }

    public void clear() {
        for (TreeMap<Integer, Identifier> texture : textures) {
            texture.clear();
        }
    }

    public boolean isEmpty(int state) {
        return textures[state].isEmpty();
    }

    public boolean isEmpty() {
        return isEmpty(state);
    }

    public Identifier next(int ticks, int state) {
        if (textures == null) return null;
        if (textures[state] == null) return null;
        Map.Entry<Integer, Identifier> entry = textures[state].higherEntry(ticks);

        if (entry == null) {
            Map.Entry<Integer, Identifier> firstEntry = textures[state].firstEntry();
            return firstEntry != null ? firstEntry.getValue() : null;
        }

        return entry.getValue();
    }

    public Identifier next(int ticks) {
        return next(ticks, state);
    }

    public Identifier next() {
        return next(ticks, state);
    }

    public Identifier get(int ticks, int state) {
        if (textures == null) return null;
        if (textures[state] == null) return null;
        Map.Entry<Integer, Identifier> entry = textures[state].floorEntry(ticks);
        if (entry == null) {
            Map.Entry<Integer, Identifier> nextEntry = textures[state].higherEntry(ticks);

            return nextEntry != null ? nextEntry.getValue() : null;
        }

        return entry.getValue();
    }

    public Identifier get(int ticks) {
        return get(ticks, state);
    }

    public Identifier get() {
        return get(ticks);
    }

    public void tick(SkinmaticsData data) {
        ticks ++;

        if (!isEmpty()) {
            if (ticks >= nextTicks && textures[state] != null) {
                currentTexture = get();

                Map.Entry<Integer, Identifier> nextEntry = textures[state].higherEntry(ticks);

                if (nextEntry != null) {
                    nextTicks = nextEntry.getKey();
                } else {
                    nextTicks = data.maxTicks;
                }
            }
        }

        if (ticks > data.maxTicks) {
            ticks = 0;
            nextTicks = -1;
        }
    }
}
