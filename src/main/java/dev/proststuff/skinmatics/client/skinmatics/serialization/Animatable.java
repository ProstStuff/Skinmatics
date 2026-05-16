package dev.proststuff.skinmatics.client.skinmatics.serialization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public record Animatable(Map<String, List<Integer>> textures) {
    public Animatable() {
        this(new HashMap<>());
    }

    public boolean isEmpty() {
        return textures == null || textures.isEmpty();
    }

    public void clear() {
        textures.clear();
    }
}
