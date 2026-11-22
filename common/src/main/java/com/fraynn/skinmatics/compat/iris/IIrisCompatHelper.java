package com.fraynn.skinmatics.compat.iris;

import com.fraynn.utilitary.platform.ServiceUtil;
import com.fraynn.utilitary.util.Tried;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;

public interface IIrisCompatHelper {
    Tried<IIrisCompatHelper> INSTANCE = Tried.tryCatch(() -> ServiceUtil.load(IIrisCompatHelper.class));

    void registerPBRLoader();
    void trackTexture(Identifier id, AbstractTexture texture);
}
