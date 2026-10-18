package dev.proststuff.skinmatics.manager;

import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.utilitary.registry.RemovableSimpleRegistry;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class AvatarTrackerManager extends RemovableSimpleRegistry<UUID, AvatarTracker> {
    public static final AvatarTrackerManager INSTANCE = new AvatarTrackerManager();

    private AvatarTrackerManager() {}

    public @Nullable AvatarTracker get(Entity entity) {
        return get(entity.getUUID());
    }

    public AvatarTracker register(UUID uuid, AvatarProfile avatarProfile) {
        if (avatarProfile == null) return null;
        AvatarTracker tracker = get(uuid);

        if (tracker != null) {
            tracker.setAvatar(avatarProfile);
            return tracker;
        }

        return register(uuid, new AvatarTracker(uuid, avatarProfile));
    }

    public AvatarTracker register(Entity entity, AvatarProfile avatarProfile) {
        return register(entity.getUUID(), avatarProfile);
    }
}
