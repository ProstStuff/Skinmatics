package com.fraynn.skinmatics;

import com.fraynn.skinmatics.gui.PaperDollRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.hud.HudLayer;

public class SkinmaticsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Skinmatics.LOGGER.info("Initializing Skinmatics: Fabric...");
        Skinmatics.init();
        SkinmaticsFabricEvents.init();
        tryAttachingPaperDoll();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void tryAttachingPaperDoll() { // Fix duplicate error
        for (HudElementRegistryImpl.RootLayer layer : HudElementRegistryImpl.ROOT_ELEMENTS.values()) {
            for (HudLayer hudLayer : layer.layers()) {
                if (hudLayer.id().equals(PaperDollRenderer.ID)) return;
            }
        }

        HudElementRegistry.attachElementAfter(VanillaHudElements.DEMO_TIMER, Skinmatics.of("paper_doll"), PaperDollRenderer.INSTANCE::extractRenderState);
    }
}
