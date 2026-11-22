package com.fraynn.skinmatics.avatar.feature.runtime;

import com.fraynn.skinmatics.avatar.AvatarTracker;
import com.fraynn.skinmatics.avatar.feature.AvatarFeatureData;
import com.fraynn.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import com.fraynn.skinmatics.avatar.feature.type.eye.EyeUtils;
import com.fraynn.skinmatics.avatar.feature.type.eye.Eyes;
import com.fraynn.utilitary.platform.services.IPlatformHelper;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EyesAvatarRuntime extends AvatarRuntime<Eyes> {
    protected Map<String, String> eyeStates = new HashMap<>();

    protected int blinkTicks;

    public EyesAvatarRuntime(AvatarTracker tracker, AvatarFeatureData<Eyes> featureData) {
        super(tracker, featureData);
        blinkTicks = -featureData.settings().blinkingTicks();
    }

    public void blink() {
        blinkTicks = tracker.tickCount + featureData.settings().blinkingTicks();
    }

    public boolean isBlinking() {
        return tracker.tickCount < blinkTicks;
    }

    public boolean isEyeClosed() {
        return isBlinking();
    }

    public @NonNull String getEyeState(String group, AvatarRenderState state) {
        return eyeStates.getOrDefault(group, isEyeClosed() ? EyeUtils.STATE_CLOSED : EyeUtils.getDynamicState(group, state));
    }

    @Override
    public void tick() {
        if (tracker.isHidden(AvatarFeatureTypes.EYES) || isBlinking()) return;
        if (IPlatformHelper.RANDOM.nextInt(featureData.settings().blinkingChance()) == 0) blink();
    }
}
