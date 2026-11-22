package com.fraynn.skinmatics.avatar.event;

import com.fraynn.skinmatics.avatar.AvatarProfile;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.utilitary.event.Event;

import java.util.UUID;

public interface AvatarApplyEvents {
    Event<Apply> APPLIED = Event.create(listeners -> (entityUUID, tracker, avatarProfile) -> {
        for (Apply event : listeners) {
            event.applied(entityUUID, tracker, avatarProfile);
        }
    });

    Event<Remove> REMOVED = Event.create(listeners -> (entityUUID, tracker, avatarProfile) -> {
        for (Remove event : listeners) {
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
