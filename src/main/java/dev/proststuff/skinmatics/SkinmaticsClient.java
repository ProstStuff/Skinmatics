package dev.proststuff.skinmatics;

import dev.proststuff.skinmatics.avatar.feature.runtime.AvatarRuntimeTypes;
import dev.proststuff.skinmatics.avatar.feature.type.AvatarFeatureTypes;
import dev.proststuff.skinmatics.avatar.renderer.PaperDollRenderer;
import dev.proststuff.skinmatics.compat.iris.IrisCompat;
import dev.proststuff.skinmatics.config.SkinmaticsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.hud.HudLayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.RandomSource;

public final class SkinmaticsClient implements ClientModInitializer {
    public static final RandomSource RANDOM = RandomSource.create();

    @Override
    public void onInitializeClient() {
        AvatarFeatureTypes.init();
        AvatarRuntimeTypes.init();

        SkinmaticsConfig.load();
        SkinmaticsClientEvents.init();
        ClientLifecycleEvents.CLIENT_STARTED.register(_ -> SkinmaticsConfig.save());
        tryAttachingPaperDoll();
        if (FabricLoader.getInstance().isModLoaded("iris")) IrisCompat.init();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void tryAttachingPaperDoll() {
        for (HudElementRegistryImpl.RootLayer layer : HudElementRegistryImpl.ROOT_ELEMENTS.values()) {
            for (HudLayer hudLayer : layer.layers()) {
                if (hudLayer.id().equals(PaperDollRenderer.ID)) return;
            }
        }

        HudElementRegistry.attachElementAfter(VanillaHudElements.DEMO_TIMER, Skinmatics.of("paper_doll"), PaperDollRenderer.INSTANCE);
    }
}