package dev.proststuff.skinmatics.avatar.event;

import dev.proststuff.skinmatics.avatar.AvatarTracker;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public interface AvatarTrackerTickEvents {
    Event<AvatarTrackerTickEvents.Pre> PRE = EventFactory.createArrayBacked(
            AvatarTrackerTickEvents.Pre.class,
            listeners -> (LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) -> {
                for (AvatarTrackerTickEvents.Pre listener : listeners) {
                    if (listener.tick(entity, tracker, isAvatarPlayer)) {
                        return true;
                    }
                }

                return false;
            }
    );

    Event<AvatarTrackerTickEvents.Post> POST = EventFactory.createArrayBacked(
            AvatarTrackerTickEvents.Post.class,
            listeners -> (LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer) -> {
                for (AvatarTrackerTickEvents.Post listener : listeners) {
                    listener.tick(entity, tracker, isAvatarPlayer);
                }
            }
    );

    interface Pre {
        /**
         * @return Cancel ticking
         */
        boolean tick(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer);
    }
    interface Post {
        void tick(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer);
    }
}
