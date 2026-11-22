package com.fraynn.skinmatics.compat.iris;


import com.fraynn.skinmatics.texture.AvatarTextureManager;
import com.fraynn.skinmatics.texture.AvatarTextureData;
import com.fraynn.skinmatics.texture.SkinmaticsDynamicTexture;
import net.irisshaders.iris.pbr.loader.PBRTextureLoader;
import net.irisshaders.iris.pbr.texture.PBRType;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.NonNull;

public class SkinmaticsPBRTextureLoader implements PBRTextureLoader<SkinmaticsDynamicTexture> {
    protected SkinmaticsPBRTextureLoader() {}

    @Override
    public void load(SkinmaticsDynamicTexture texture, ResourceManager resourceManager, PBRTextureConsumer pbrTextureConsumer) {
        Identifier location = IrisCompat.TEXTURES.get(texture);
        if (location == null) return;

        ImmediateState.temporarilyIgnorePass = true;
        AvatarTextureData normalTexture = getPBRTexture(location, PBRType.NORMAL);
        AvatarTextureData specularTexture = getPBRTexture(location, PBRType.SPECULAR);
        ImmediateState.temporarilyIgnorePass = false;

        if (!normalTexture.isEmpty()) pbrTextureConsumer.acceptNormalTexture(normalTexture.texture());
        if (!specularTexture.isEmpty()) pbrTextureConsumer.acceptSpecularTexture(specularTexture.texture());
    }

    protected @NonNull AvatarTextureData getPBRTexture(Identifier imageLocation, PBRType pbrType) {
        return AvatarTextureManager.INSTANCE.get(imageLocation.withPath(pbrType::appendSuffix));
    }
}