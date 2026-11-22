package com.fraynn.skinmatics;

import com.fraynn.skinmatics.avatar.pack.AvatarPackLoader;
import com.fraynn.skinmatics.entity.renderer.layer.EmissiveSkinRenderLayer;
import com.fraynn.skinmatics.entity.renderer.layer.EyesRenderLayer;
import com.fraynn.skinmatics.config.SkinmaticsConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public interface SkinmaticsFabricEvents {
    static void init() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Skinmatics.of("skinmatics_pack_loader"), AvatarPackLoader::onLoad);
        ClientLifecycleEvents.CLIENT_STARTED.register(_ -> SkinmaticsConfig.save());

        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsEvents::tickClient);
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> SkinmaticsEvents.connected(c));
        ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> SkinmaticsEvents.disconnected(b));
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(SkinmaticsFabricEvents::registerPlayerFeatures);
        ClientTickEvents.END_LEVEL_TICK.register((a) -> SkinmaticsEvents.tickLevel());
        ClientEntityEvents.ENTITY_LOAD.register((a, b) -> SkinmaticsEvents.entityLoad(a));
        ClientEntityEvents.ENTITY_UNLOAD.register((a, b) -> SkinmaticsEvents.entityUnload(a));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerPlayerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> livingEntityRenderer, LivingEntityRenderLayerRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if (livingEntityRenderer instanceof AvatarRenderer avatarRenderer) {
            registrationHelper.register(new EyesRenderLayer(avatarRenderer));
            registrationHelper.register(new EmissiveSkinRenderLayer(avatarRenderer));
        }
    }
}