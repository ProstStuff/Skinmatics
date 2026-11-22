package com.fraynn.skinmatics.avatar.event;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.utilitary.event.Event;
import net.minecraft.world.entity.LivingEntity;

public interface AvatarTrackerTickEvents {
    Event<Pre> PRE = Event.create(
            listeners -> (LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) -> {
                for (Pre listener : listeners) {
                    if (listener.tick(entity, tracker, isAvatarPlayer)) {
                        return true;
                    }
                }

                return false;
            }
    );

    Event<Post> POST = Event.create(
            listeners -> (LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) -> {
                for (Post listener : listeners) {
                    listener.tick(entity, tracker, isAvatarPlayer);
                }
            }
    );

    interface Pre {
        boolean tick(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer);
    }
    interface Post {
        void tick(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer);
    }
}
