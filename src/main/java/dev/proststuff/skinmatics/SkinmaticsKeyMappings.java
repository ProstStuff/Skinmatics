package dev.proststuff.skinmatics;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public interface SkinmaticsKeyMappings {
    KeyMapping.Category SKINMATICS = KeyMapping.Category.register(Skinmatics.of("main"));

    KeyMapping OPEN_PROFILES = register("open_avatars", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U);
    KeyMapping SET_PROFILE_ENTITY = register("set_avatar", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);
    KeyMapping UNSET_PROFILE_ENTITY = register("unset_avatar", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);

    private static KeyMapping register(String name, InputConstants.Type type, int bind) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.skinmatics." + name,
                type,
                bind,
                SKINMATICS
        ));
    }

    static void init() {}
}