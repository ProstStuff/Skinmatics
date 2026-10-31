package dev.proststuff.skinmatics.avatar.event;

import dev.proststuff.skinmatics.avatar.AvatarProfile;
import dev.proststuff.skinmatics.avatar.AvatarTracker;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.UUID;

public interface AvatarApplyEvents {
    Event<AvatarApplyEvents.Apply> APPLY = EventFactory.createArrayBacked(AvatarApplyEvents.Apply.class, (listeners) -> (entityUUID, tracker, avatarProfile) -> {
        for (AvatarApplyEvents.Apply event : listeners) {
            event.applied(entityUUID, tracker, avatarProfile);
        }
    });

    Event<AvatarApplyEvents.Remove> REMOVE = EventFactory.createArrayBacked(AvatarApplyEvents.Remove.class, (listeners) -> (entityUUID, tracker, avatarProfile) -> {
        for (AvatarApplyEvents.Remove event : listeners) {
            event.removed(entityUUID, tracker, avatarProfile);
        }
    });

    interface Apply {
        void applied(UUID entityUUID, AvatarTracker tracker, AvatarProfile avatarProfile);
    }

    interface Remove {
        void removed(UUID entityUUID, AvatarTracker tracker, AvatarProfile avatarProfile);
    }
}
