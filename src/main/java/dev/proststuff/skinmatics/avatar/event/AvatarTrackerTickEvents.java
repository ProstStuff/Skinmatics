package dev.proststuff.skinmatics.avatar.event;

import dev.proststuff.skinmatics.avatar.AvatarTracker;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AvatarTrackerTickEvents {
    Event<AvatarTrackerTickEvents.Pre> PRE = EventFactory.createArrayBacked(
            AvatarTrackerTickEvents.Pre.class,
            listeners -> (AvatarTracker tracker) -> {
                for (AvatarTrackerTickEvents.Pre listener : listeners) {
                    listener.tick(tracker);
                }
            }
    );

    Event<AvatarTrackerTickEvents.Post> POST = EventFactory.createArrayBacked(
            AvatarTrackerTickEvents.Post.class,
            listeners -> (AvatarTracker tracker) -> {
                for (AvatarTrackerTickEvents.Post listener : listeners) {
                    listener.tick(tracker);
                }
            }
    );

    interface Pre extends AvatarTrackerTickEvents {}

    interface Post extends AvatarTrackerTickEvents {}

    void tick(AvatarTracker tracker);
}
