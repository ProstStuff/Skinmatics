package dev.proststuff.skinmatics.mixin.client.skin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allow (online / Mojang) player skins to have transparency
 */
@Mixin(PlayerSkinTextureDownloader.class)
public class PlayerSkinDownloaderMixin {
    @Inject(method = "stripAlpha", at = @At("HEAD"), cancellable = true)
    private static void cancelStripAlpha(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "stripColor", at = @At("HEAD"), cancellable = true)
    private static void cancelStripColor(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
        ci.cancel();
    }
}
