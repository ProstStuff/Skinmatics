package dev.proststuff.skinmatics.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.ProfileManager;
import dev.proststuff.skinmatics.api.client.ProfileRuntime;
import dev.proststuff.skinmatics.api.client.ProfileRuntimeManager;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarPreviewRenderer;
import dev.proststuff.skinmatics.client.model.EmissiveSkinRenderLayer;
import dev.proststuff.skinmatics.client.model.EyeRenderLayer;
import dev.proststuff.utilitary.api.utility.FileJsonUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkinmaticsClientEvents {
    private static final SkinmaticsConfig CONFIG = SkinmaticsConfig.INSTANCE;
    private static int cleanUpTime = 0;

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(SkinmaticsClientEvents::clientInitialized);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerPlayerFeatures);
        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsClientEvents::tickAvatarPreviewAndClean);
        ClientTickEvents.START_LEVEL_TICK.register(SkinmaticsClientEvents::tickRuntimes);
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Skinmatics.of("skinmatics_textures"), SkinmaticsClientEvents::onResourceReload);
        ClientCommandRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerCommands);
        ClientEntityEvents.ENTITY_LOAD.register(SkinmaticsClientEvents::entityLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(SkinmaticsClientEvents::entityUnload);
        ClientPlayConnectionEvents.JOIN.register(SkinmaticsClientEvents::joined);
        ClientPlayConnectionEvents.DISCONNECT.register(SkinmaticsClientEvents::left);
    }

    private static CompletableFuture<Void> onResourceReload(PreparableReloadListener.SharedState sharedState, Executor executor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor applyExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait).thenRunAsync(() -> SkinmaticsClient.TEXTURE_MANAGER.reload(), applyExecutor);
    }

    private static void clientInitialized(Minecraft minecraft) {
        SkinmaticsClient.CONFIG.load();
        SkinmaticsClient.TEXTURE_MANAGER = new SkinmaticsTextureManager();
        SkinmaticsClient.CONFIG.save();
    }

    @SuppressWarnings("unchecked")
    private static void registerPlayerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> livingEntityRenderer, LivingEntityRenderLayerRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if (livingEntityRenderer instanceof AvatarRenderer avatarRenderer) {
            registrationHelper.register(new EyeRenderLayer(avatarRenderer, EyeRenderLayer.EyePosition.RIGHT));
            registrationHelper.register(new EyeRenderLayer(avatarRenderer, EyeRenderLayer.EyePosition.LEFT));

            registrationHelper.register(new EmissiveSkinRenderLayer(avatarRenderer));
        }
    }

    private static void tickRuntimes(ClientLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;

        if (localPlayer == null) return;
        RandomSource random = level.getRandom();
        Map<UUID, Profile> assignedProfiles = ProfileManager.getAllAssigned();
        BlockPos clientPos = localPlayer.getOnPos();
        int updateRange = CONFIG.updateRange.get();

        for (Map.Entry<UUID, Profile> entry : assignedProfiles.entrySet()) {
            UUID uuid = entry.getKey();
            Entity entity = level.getEntity(uuid);

            if (entity != AvatarPreviewRenderer.PREVIEWER && entity instanceof LivingEntity living) {
                Profile profile = entry.getValue();
                BlockPos entityPosition = living.getOnPos();

                if (profile.showEyes.get()) {
                    if (entityPosition.distSqr(clientPos) > (updateRange * updateRange)) continue;
                    ProfileRuntime runtime = ProfileRuntimeManager.get(entity);

                    if (runtime != null) {
                        runtime.tick(entity, random);
                    }
                }
            }
        }
    }

    private static void tickAvatarPreviewAndClean(Minecraft minecraft) {
        RemotePlayer previewer = AvatarPreviewRenderer.PREVIEWER;
        ClientLevel level = minecraft.level;

        if (level != null) {
            if (previewer != null) {
                RandomSource random = previewer.getRandom();
                ProfileRuntime runtime = ProfileRuntimeManager.get(previewer);

                if (runtime != null) {
                    previewer.tick();
                    runtime.tick(previewer, random);
                    previewer.tickCount ++;
                }
            }

            cleanUpTime --;

            if (cleanUpTime <= 0) {
                ProfileManager.cleanUp(level);
                cleanUpTime = 200;
            }
        } else if (AvatarPreviewRenderer.PREVIEWER != null) {
            ProfileManager.unassign(AvatarPreviewRenderer.PREVIEWER);
            AvatarPreviewRenderer.PREVIEWER.remove(Entity.RemovalReason.DISCARDED);
            AvatarPreviewRenderer.PREVIEWER = null;
        }
    }

    private static void entityLoad(Entity entity, ClientLevel level) {
        if (entity instanceof Avatar && entity != Minecraft.getInstance().player) {
            String name = entity.getName().getString();
            String uuidString = entity.getUUID().toString();

            for (Map.Entry<String, String> entry : SkinmaticsConfig.INSTANCE.autoAssign.get().entrySet()) {
                String target = entry.getKey();
                String profileName = entry.getValue();

                Profile profile = ProfileManager.get(profileName);

                if ((target.equals(name) || uuidString.equals(name)) && profile != null) {
                    ProfileManager.assign(entity, profile);
                }
            }
        }
    }

    private static void entityUnload(Entity entity, ClientLevel level) {
        if (entity instanceof Avatar && entity != Minecraft.getInstance().player) {
            ProfileManager.unassign(entity);
        }
    }

    private static void joined(ClientPacketListener listener, PacketSender sender, Minecraft minecraft) {
        ProfileManager.scanAndLoad();
    }

    private static void left(ClientPacketListener listener, Minecraft minecraft) {
        ProfileManager.clear();

        if (AvatarPreviewRenderer.PREVIEWER != null) {
            ProfileManager.unassign(AvatarPreviewRenderer.PREVIEWER);
            AvatarPreviewRenderer.PREVIEWER.remove(Entity.RemovalReason.DISCARDED);
            AvatarPreviewRenderer.PREVIEWER = null;
        }
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(ClientCommands.literal("skinmatics")
                .then(ClientCommands.literal("profile")
                        .then(ClientCommands.literal("open")
                                .executes(context -> {
                                    Util.getPlatform().openPath(FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles"));
                                    context.getSource().sendFeedback(Component.literal("Opening profiles"));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("create")
                                .then(ClientCommands.argument("profile", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "profile");
                                            Profile profile = ProfileManager.load(name);
                                            profile.save();

                                            Util.getPlatform().openFile(profile.getDestination().toFile());
                                            context.getSource().sendFeedback(Component.literal(String.format("Created %s profile (%s.json) ", name, name)));
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommands.literal("load")
                                .then(ClientCommands.argument("profile", StringArgumentType.string())
                                        .executes(context -> {
                                            String profileName = StringArgumentType.getString(context, "profile");
                                            SkinmaticsConfig.INSTANCE.profile.set(profileName);
                                            ProfileManager.loadCurrent(context.getSource().getPlayer());
                                            SkinmaticsConfig.INSTANCE.save();
                                            context.getSource().sendFeedback(Component.literal(String.format("Loaded %s profile to self", profileName)));
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommands.literal("reload")
                                .executes(context -> {
                                    ProfileManager.scanAndLoad();
                                    context.getSource().sendFeedback(Component.literal(String.format("Reloaded %s profiles", ProfileManager.getAll().size())));
                                    return 1;
                                })
                        )
                        .then(ClientCommands.literal("assign")
                                .then(ClientCommands.argument("profile", StringArgumentType.string())
                                        .executes(context -> {
                                            Minecraft minecraft = context.getSource().getClient();

                                            if (!(minecraft.hitResult instanceof EntityHitResult entityHitResult)) {
                                                context.getSource().sendError(Component.literal("No entity is targeted"));
                                                return 0;
                                            }

                                            Entity entity = entityHitResult.getEntity();
                                            String name = StringArgumentType.getString(context, "profile");
                                            Profile profile = ProfileManager.get(name);

                                            if (profile == null) {
                                                context.getSource().sendError(Component.literal(String.format("Profile %s not found", name)));
                                                return 0;
                                            }

                                            Profile oldData = ProfileManager.assign(entity, profile);
                                            String entityName = entity.getName().getString();

                                            if (oldData == null) {
                                                context.getSource().sendFeedback(Component.literal(String.format("Assigned %s to %s", name, entityName)));
                                            } else {
                                                context.getSource().sendFeedback(Component.literal(String.format("Assigned %s to %s, previously assigned to %s", name, entityName, oldData.getName())));
                                            }

                                            return 1;
                                        }))
                        )

                        .then(ClientCommands.literal("unassign")
                                .executes(context -> {
                                    Minecraft minecraft = context.getSource().getClient();

                                    if (!(minecraft.hitResult instanceof EntityHitResult entityHitResult)) {
                                        context.getSource().sendError(Component.literal("No entity is targeted"));
                                        return 0;
                                    }

                                    Entity entity = entityHitResult.getEntity();
                                    Profile data = ProfileManager.unassign(entity);

                                    if (data != null) {
                                        context.getSource().sendFeedback(Component.literal(String.format("Removed %s from %s", data.getName(), entity.getName().getString())));
                                    } else {
                                        context.getSource().sendError(Component.literal("No profile found in this entity"));
                                        return 0;
                                    }

                                    return 1;
                                })
                        )

                        .then(ClientCommands.literal("documentation")
                                .executes(context -> {
                                    Util.getPlatform().openUri("https://github.com/ProstStuff/Skinmatics");
                                    context.getSource().sendFeedback(Component.literal("Opening online documentation"));
                                    return 1;
                                })
                        )
                )

                .then(ClientCommands.literal("config")
                        .then(ClientCommands.literal("open")
                                .executes(context -> {
                                    Util.getPlatform().openFile(SkinmaticsConfig.INSTANCE.getDestination().toFile());
                                    context.getSource().sendFeedback(Component.literal("Opening Skinmatics config"));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("reload")
                                .executes(context -> {
                                    SkinmaticsConfig.INSTANCE.load();
                                    context.getSource().sendFeedback(Component.literal("Reloaded Skinmatics config"));
                                    return 1;
                                })
                        )
                )

                .then(ClientCommands.literal("texture")
                        .then(ClientCommands.literal("open")
                                .executes(context -> {
                                    Util.getPlatform().openPath(FileJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles"));
                                    context.getSource().sendFeedback(Component.literal("Opening textures"));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("reload")
                                .executes(context -> {
                                    Minecraft.getInstance().reloadResourcePacks().whenComplete((_, _) -> context.getSource().sendFeedback(Component.literal("Reloaded textures")));
                                    return 1;
                                }))
                )

                .then(ClientCommands.literal("gui")
                        .executes(context -> {
                            Minecraft minecraft = context.getSource().getClient();
                            Screen screen = minecraft.screen;
                            minecraft.setScreen(SkinmaticsClient.openCustomizationScreen(screen));
                            return 1;
                        })
                )
        );
    }
}
