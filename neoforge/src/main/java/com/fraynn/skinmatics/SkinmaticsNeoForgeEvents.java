package com.fraynn.skinmatics;

import com.fraynn.skinmatics.avatar.pack.AvatarPackLoader;
import com.fraynn.skinmatics.gui.PaperDollRenderer;
import com.fraynn.skinmatics.entity.renderer.layer.EmissiveSkinRenderLayer;
import com.fraynn.skinmatics.entity.renderer.layer.EyesRenderLayer;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.PlayerModelType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@EventBusSubscriber(modid = Skinmatics.ID, value = Dist.CLIENT)
public class SkinmaticsNeoForgeEvents {
    @SubscribeEvent
    public static void registerReloads(AddClientReloadListenersEvent event) {
        event.addListener(Skinmatics.of("skinmatics_pack_loader"), AvatarPackLoader::onLoad);
    }

    @SubscribeEvent
    public static void clientStarted(ClientStartedEvent event) {
        SkinmaticsConfig.save();
    }

    @SubscribeEvent
    public static void renderHud(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.DEMO_OVERLAY, Skinmatics.of("paper_doll"), PaperDollRenderer.INSTANCE::extractRenderState);
    }

    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post event) {
        SkinmaticsEvents.tickClient(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        SkinmaticsEvents.connected(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        SkinmaticsEvents.disconnected(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event)
    {
        for (PlayerModelType modelType : event.getSkins()) {
            addLayer(event.getPlayerRenderer(modelType), EyesRenderLayer::new);
            addLayer(event.getPlayerRenderer(modelType), EmissiveSkinRenderLayer::new);

            addLayer(event.getMannequinRenderer(modelType), EyesRenderLayer::new);
            addLayer(event.getMannequinRenderer(modelType), EmissiveSkinRenderLayer::new);
        }
    }

    private static void addLayer(@Nullable AvatarRenderer<?> renderer, Function<AvatarRenderer<?>, RenderLayer<AvatarRenderState, PlayerModel>> layerFactory) {
        if (renderer != null) renderer.addLayer(layerFactory.apply(renderer));
    }

    @SubscribeEvent
    public static void levelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            SkinmaticsEvents.tickLevel();
        }
    }

    @SubscribeEvent
    public static void entityLoad(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            SkinmaticsEvents.entityLoad(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void entityUnload(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            SkinmaticsEvents.entityUnload(event.getEntity());
        }
    }
}
