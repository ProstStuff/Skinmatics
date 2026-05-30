package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.client.impl.mixin.SlimAccessor;
import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerModel.class)
public class PlayerModelSlimAccessor implements SlimAccessor {
    @Shadow
    @Final
    private boolean slim;

    @Override
    public boolean skinmatics$isSlim() {
        return slim;
    }
}
