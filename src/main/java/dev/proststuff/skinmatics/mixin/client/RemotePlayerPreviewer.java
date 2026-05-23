package dev.proststuff.skinmatics.mixin.client;

import dev.proststuff.skinmatics.api.client.impl.EntityPreviewer;
import net.minecraft.client.player.RemotePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RemotePlayer.class)
public class RemotePlayerPreviewer implements EntityPreviewer {
    @Unique protected boolean skinmatics$previewing = false;

    @Override
    public void skinmatics$preview() {
        this.skinmatics$previewing = true;
    }

    @Override
    public boolean skinmatics$isPreviewing() {
        return skinmatics$previewing;
    }
}
