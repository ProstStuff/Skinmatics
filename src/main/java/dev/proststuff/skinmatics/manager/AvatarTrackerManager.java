package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.avatar.v1.data.AvatarData;
import dev.proststuff.skinmatics.avatar.v1.AvatarTracker;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AvatarTrackerManager implements Manageable<UUID, AvatarTracker> {
    public static final AvatarTrackerManager INSTANCE = new AvatarTrackerManager();

    private final Map<UUID, AvatarTracker> trackers = new HashMap<>();
    private final Map<AvatarTracker, UUID> inverseTrackers = new HashMap<>();

    private AvatarTrackerManager() {}

    public @Nullable AvatarTracker get(Entity entity) {
        return get(entity.getUUID());
    }

    @Override
    public Map<UUID, AvatarTracker> getAll() {
        return trackers;
    }

    @Override
    public Map<AvatarTracker, UUID> getInverse() {
        return inverseTrackers;
    }

    public AvatarTracker register(UUID uuid, AvatarData avatarData) {
        if (avatarData == null) return null;
        AvatarTracker tracker = get(uuid);

        if (tracker != null) {
            tracker.setAvatar(avatarData);
            return tracker;
        }

        return register(uuid, new AvatarTracker(uuid, avatarData));
    }

    public AvatarTracker register(Entity entity, AvatarData avatarData) {
        return register(entity.getUUID(), avatarData);
    }
}
