package dev.proststuff.skinmatics.compat;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarTrackerManager;
import dev.proststuff.skinmatics.client.skinmatics.avatar.AvatarTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class SkinmaticsDynamicLights implements DynamicLightsInitializer {
    public static final EntityLuminance.Type PROFILED_ENTITY = EntityLuminance.Type.registerSimple(
            SkinmaticsMod.of("profiled_entity"),
            ProfileEntityLuminance.INSTANCE
    );

    @Override
    public void onInitializeDynamicLights(@NonNull DynamicLightsContext init) {
        init.entityLightSourceManager().onRegisterEvent().register(context -> {
            context.register(EntityType.PLAYER, ProfileEntityLuminance.INSTANCE);
            context.register(EntityType.MANNEQUIN, ProfileEntityLuminance.INSTANCE);
        });
    }

    public static final class ProfileEntityLuminance implements EntityLuminance {
        public static final ProfileEntityLuminance INSTANCE = new ProfileEntityLuminance();

        private ProfileEntityLuminance() {}

        @Override
        public @NonNull Type type() {
            return PROFILED_ENTITY;
        }

        @Override
        public @Range(from = 0L, to = 15L) int getLuminance(@NonNull ItemLightSourceManager itemLightSourceManager, @NonNull Entity entity) {
            AvatarTracker tracker = AvatarTrackerManager.INSTANCE.get(entity);
            return tracker != null ? tracker.getAvatar().lights().get() : 0;
        }
    }
}