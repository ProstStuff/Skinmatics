package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinTextureDownloader.class)
public class PreventOnlineSkinModification {
    @Inject(method = "doNotchTransparencyHack", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$cancelStripAlpha(NativeImage image, int x0, int y0, int x1, int y1, CallbackInfo ci) {
        ci.cancel();

    }

    @Inject(method = "setNoAlpha", at = @At("HEAD"), cancellable = true)
    private static void skinmatics$cancelStripColor(NativeImage image, int x1, int y1, int x2, int y2, CallbackInfo ci) {
        ci.cancel();
    }
}
