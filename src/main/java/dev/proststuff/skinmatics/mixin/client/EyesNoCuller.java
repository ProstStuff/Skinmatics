package dev.proststuff.skinmatics.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RenderPipeline.Builder.class)
public abstract class EyesNoCuller {
    @Shadow
    private Optional<Identifier> location;

    @Shadow
    public abstract RenderPipeline.Builder withCull(boolean cull);

    @Inject(method = "build", at = @At("HEAD"))
    public void setNoCullForEye(CallbackInfoReturnable<RenderPipeline> cir) {
        location.ifPresent(id -> {
            if (id.toString().equals("minecraft:pipeline/eyes")) {
                withCull(false);
            }
        });
    }
}
