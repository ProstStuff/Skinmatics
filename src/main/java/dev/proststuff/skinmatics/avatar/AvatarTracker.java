package dev.proststuff.skinmatics.avatar;

import dev.proststuff.skinmatics.avatar.feature.AvatarFeatureData;
import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntime;
import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntimeType;
import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntimeTypeRegistry;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureType;
import dev.proststuff.skinmatics.entity.AvatarPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class AvatarTracker implements AvatarIdentity {
    private final UUID uuid;

    private @NonNull AvatarProfile avatarProfile;
    private final Map<AvatarRuntimeType<?, ?>, AvatarRuntime<?>> runtimes = new HashMap<>();
    public int tickCount;

    public AvatarTracker(UUID uuid, @NonNull AvatarProfile avatarProfile) {
        this.uuid = uuid;
        this.tickCount = 0;
        setAvatar(avatarProfile);
    }

    public AvatarTracker(@NonNull Entity entity, @NonNull AvatarProfile avatarProfile) {
        this(entity.getUUID(), avatarProfile);
    }

    public void setAvatar(@NonNull AvatarProfile avatarProfile) {
        runtimes.clear();
        this.avatarProfile = avatarProfile;

        for (Map.Entry<Identifier, AvatarFeatureData<?>> entry : avatarProfile.features().entrySet()) {
            AvatarRuntimeType<?, ?> runtimeType = AvatarRuntimeTypeRegistry.get(entry.getKey());
            if (runtimeType != null) createRuntime(entry.getValue(), runtimeType);
        }
    }

    @SuppressWarnings("unchecked")
    private <S, R extends AvatarRuntime<S>> void createRuntime(AvatarFeatureData<?> featureData, AvatarRuntimeType<S, R> runtimeType) {
        runtimes.put(runtimeType, runtimeType.factory().create(this, (AvatarFeatureData<S>) featureData));
    }

    public @NonNull AvatarProfile getAvatar() {
        return avatarProfile;
    }

    @SuppressWarnings("unchecked")
    public <S, R extends AvatarRuntime<S>> @Nullable R getRuntime(AvatarRuntimeType<S, R> avatarFeature) {
        return (R) runtimes.get(avatarFeature);
    }

    public void tick() {
        for (Map.Entry<AvatarRuntimeType<?, ?>, AvatarRuntime<?>> entry : runtimes.entrySet()) {
            if (entry.getKey().ticking()) entry.getValue().tick();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public @Nullable LivingEntity getEntity() {
        return getEntityClient(uuid);
    }

    public static @Nullable LivingEntity getEntityClient(UUID uuid) {
        ClientLevel level = Minecraft.getInstance().level;
        Objects.requireNonNull(level);
        Entity entity = level.getEntity(uuid);

        if (uuid.equals(AvatarPlayer.DEFAULT_AVATAR_PROFILE.id())) return AvatarPlayer.getInstance();
        if (entity == null) return null;
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("AvatarTracker shouldn't track a non-living entity!");
        }

        return (LivingEntity) entity;
    }

    @Override
    public @Nullable <S> AvatarFeatureData<S> getFeature(AvatarFeatureType<S> avatarFeature) {
        return avatarProfile.getFeature(avatarFeature);
    }
}