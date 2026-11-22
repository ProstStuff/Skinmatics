package com.fraynn.skinmatics.compat;

import com.fraynn.skinmatics.Skinmatics;
import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.runtime.AvatarRuntimeTypes;
import com.fraynn.skinmatics.avatar.feature.runtime.light.LightAvatarRuntime;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.Light;
import com.fraynn.skinmatics.avatar.AvatarTrackerManager;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public class SkinmaticsDynamicLights implements DynamicLightsInitializer {
    public static final EntityLuminance.Type PROFILED_ENTITY = EntityLuminance.Type.registerSimple(
            Skinmatics.of("profiled_entity"),
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
            if (tracker == null) return 0;
            AvatarFeatureData<Light<?>> featureData = tracker.getFeature(AvatarFeatureTypes.LIGHTS);
            if (featureData == null) return 0;
            LightAvatarRuntime avatarRuntime = tracker.getRuntime(AvatarRuntimeTypes.LIGHTS);
            return avatarRuntime != null ? avatarRuntime.get() : 0;
        }
    }
}