package dev.proststuff.skinmatics.client.skinmatics;

import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class LayerState {
    protected TreeMap<Integer, Identifier>[] textures;
    protected Identifier[] currentStateTextures;
    protected int[] nextStateTicks;

    public LayerState(int size) {
        textures = new TreeMap[size];
        currentStateTextures = new Identifier[size];
        nextStateTicks = new int[size];

        Arrays.fill(nextStateTicks, 0);

        for (int i = 0; i < textures.length; i++) {
            textures[i] = new TreeMap<>();
        }
    }

    public TreeMap<Integer, Identifier> getState(int state) {
        return textures[state];
    }

    public Identifier getTexture(int state) {
        Identifier texture = currentStateTextures[state];

        if (texture == null && !textures[state].isEmpty()) {
            texture = textures[state].pollFirstEntry().getValue();
            currentStateTextures[state] = texture;
        }

        return texture;
    }

    public void clear() {
        for (int state = 0; state < textures.length; state++) {
            TreeMap<Integer, Identifier> texture = textures[state];
            texture.clear();
            nextStateTicks[state] = 0;
            currentStateTextures[state] = null;
        }
    }

    public void reset() {
        for (int state = 0; state < textures.length; state++) {
            TreeMap<Integer, Identifier> texture = textures[state];
            if (texture.isEmpty()) continue;
            Map.Entry<Integer, Identifier> first = texture.firstEntry();
            nextStateTicks[state] = first.getKey();
            currentStateTextures[state] = first.getValue();
        }
    }

    public void tick(int ticks) {
        for (int state = 0; state < textures.length; state++) {
            TreeMap<Integer, Identifier> texture = textures[state];
            if (texture.isEmpty()) continue;

            if (texture.size() == 1) {
                if (currentStateTextures[state] == null) {
                    currentStateTextures[state] = texture.firstEntry().getValue();
                }
            } else {
                if (ticks == nextStateTicks[state]) {
                    Map.Entry<Integer, Identifier> currentEntry = texture.floorEntry(ticks);
                    Map.Entry<Integer, Identifier> nextEntry = texture.higherEntry(ticks);

                    if (currentEntry == null) {
                        currentStateTextures[state] = texture.lastEntry().getValue();
                    } else {
                        currentStateTextures[state] = currentEntry.getValue();
                    }

                    if (nextEntry != null) {
                        nextStateTicks[state] = nextEntry.getKey();
                    } else {
                        nextStateTicks[state] = texture.firstKey();
                    }
                }
            }
        }
    }
}