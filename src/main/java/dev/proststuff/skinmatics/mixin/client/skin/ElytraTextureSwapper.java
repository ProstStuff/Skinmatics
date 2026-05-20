package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.impl.ProfileGetter;
import dev.proststuff.skinmatics.api.skinmatics.Profile;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WingsLayer.class)
public class ElytraTextureSwapper {
    @Inject(method = "getPlayerElytraTexture", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$modifyDefaultElytraTexture(HumanoidRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof ProfileGetter dataHolder) {
            Profile profile = dataHolder.skinmatics$getProfile();

            if (profile != null) {
                Identifier identifier = profile.data.getElytra(false);
                if (identifier != null) {
                    cir.setReturnValue(identifier);
                }
            }
        }
    }
}
