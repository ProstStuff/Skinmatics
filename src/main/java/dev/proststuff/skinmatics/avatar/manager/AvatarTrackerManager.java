package dev.proststuff.skinmatics.avatar.manager;

import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import dev.proststuff.skinmatics.avatar.event.AvatarApplyEvents;
import dev.proststuff.utilitary.registry.RemovableSimpleRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AvatarTrackerManager extends RemovableSimpleRegistry<UUID, AvatarTracker> {
    public static final AvatarTrackerManager INSTANCE = new AvatarTrackerManager();

    private AvatarTrackerManager() {}

    public @Nullable AvatarTracker get(Entity entity) {
        return get(entity.getUUID());
    }

    @Override
    public AvatarTracker register(UUID key, AvatarTracker value) {
        AvatarTracker tracker = super.register(key, value);
        AvatarApplyEvents.APPLY.invoker().applied(key, tracker, tracker.getAvatar());
        return tracker;
    }

    public AvatarTracker register(UUID uuid, AvatarProfile avatarProfile) {
        if (avatarProfile == null) return null;
        AvatarTracker tracker = get(uuid);

        if (tracker != null) {
            tracker.setAvatar(avatarProfile);
            AvatarApplyEvents.APPLY.invoker().applied(uuid, tracker, avatarProfile);
            return tracker;
        }

        return register(uuid, new AvatarTracker(uuid, avatarProfile));
    }

    public AvatarTracker register(LivingEntity entity, AvatarProfile avatarProfile) {
        return register(entity.getUUID(), avatarProfile);
    }

    public void unregisterAll(AvatarProfile avatarProfile) {
        Set<UUID> uuids = new HashSet<>();

        for (Entry<UUID, AvatarTracker> entry : entries) {
            if (entry.value().getAvatar().equals(avatarProfile)) {
                uuids.add(entry.key());
            }
        }

        uuids.forEach(this::unregister);
    }

    @Override
    public AvatarTracker unregister(UUID key) {
        AvatarTracker tracker = super.unregister(key);
        AvatarApplyEvents.REMOVE.invoker().removed(key, tracker, tracker.getAvatar());
        return tracker;
    }

    @Override
    public UUID unregisterValue(AvatarTracker value) {
        UUID uuid = super.unregisterValue(value);
        if (uuid != null) AvatarApplyEvents.REMOVE.invoker().removed(uuid, value, value.getAvatar());
        return uuid;
    }
}
