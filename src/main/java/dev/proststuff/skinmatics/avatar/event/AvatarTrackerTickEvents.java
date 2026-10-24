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
                    listener.tick(entity, tracker, isAvatarPlayer);
                }
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

    interface Pre extends AvatarTrackerTickEvents {}

    interface Post extends AvatarTrackerTickEvents {}

    void tick(LivingEntity entity, AvatarTracker tracker, boolean isAvatarPlayer);
}
