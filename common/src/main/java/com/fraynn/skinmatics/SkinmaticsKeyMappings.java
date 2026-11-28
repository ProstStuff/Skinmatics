package com.fraynn.skinmatics;

import com.fraynn.utilitary.platform.services.IClientHelper;
import com.fraynn.utilitary.util.Phantom;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public interface SkinmaticsKeyMappings {
    KeyMapping.Category SKINMATICS = KeyMapping.Category.register(Skinmatics.of("main"));

    Phantom<KeyMapping> OPEN_PROFILES = register("open_avatars",InputConstants.Type.KEYBOARD, InputConstants.KEY_U);
    //Phantom<KeyMapping> OPEN_WHEELS = register("open_wheels",InputConstants.Type.KEYBOARD, InputConstants.KEY_X);
    Phantom<KeyMapping> SET_PROFILE_ENTITY = register("set_avatar", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue());
    Phantom<KeyMapping> UNSET_PROFILE_ENTITY = register("unset_avatar", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue());

    private static Phantom<KeyMapping> register(String name, InputConstants.Type type, int bind) {
        return IClientHelper.INSTANCE.registerKeyMapping(
                Skinmatics.ID,
                "key.skinmatics." + name,
                type,
                bind,
                SKINMATICS
        );
    }

    static void init() {}
}