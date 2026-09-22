package dev.proststuff.skinmatics.client.manager;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface Manageable<K, V> {
    Map<K, V> getAll();
    Map<V, K> getInverse();

    default @Nullable V get(K key) {
        return getAll().get(key);
    }

    default @Nullable K getKey(V value) {
        return getInverse().get(value);
    }

    default V register(K key, V value) {
        getAll().put(key, value);
        getInverse().put(value, key);
        return value;
    }

    default V unregister(K key) {
        V v = getAll().remove(key);
        getInverse().remove(v);
        return v;
    }

    default void unregisterAll() {
        List.copyOf(getAll().keySet()).forEach(this::unregister);
    }
}
