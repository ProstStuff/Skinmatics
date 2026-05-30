package dev.proststuff.skinmatics.client.gui.customization.avatar;

import com.mojang.authlib.GameProfile;
import dev.proststuff.skinmatics.api.client.impl.mixin.EntityPreviewer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;

import java.util.UUID;

public class PreviewPlayer extends RemotePlayer implements EntityPreviewer {
    public PreviewPlayer(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
        setUUID(UUID.randomUUID());
    }

    @Override
    public void skinmatics$preview() {

    }

    @Override
    public boolean skinmatics$isPreviewing() {
        return true;
    }
}
