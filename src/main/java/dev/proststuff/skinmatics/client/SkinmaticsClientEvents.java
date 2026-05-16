package dev.proststuff.skinmatics.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.Profile;
import dev.proststuff.skinmatics.client.model.EmissiveSkinFeatureRenderer;
import dev.proststuff.skinmatics.client.model.EyeFeatureRenderer;
import dev.proststuff.skinmatics.client.skinmatics.ProfileHandler;
import dev.proststuff.utilitary.utility.UtilitaryJsonUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
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

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(SkinmaticsClientEvents::clientInitialized);
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerPlayerFeatures);
        ClientTickEvents.END_CLIENT_TICK.register(SkinmaticsClientEvents::clientTicked);
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Skinmatics.of("skinmatics_textures"), SkinmaticsClientEvents::onResourceReload);
        ClientCommandRegistrationCallback.EVENT.register(SkinmaticsClientEvents::registerCommands);
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
            registrationHelper.register(new EyeFeatureRenderer(avatarRenderer, EyeFeatureRenderer.EyePosition.RIGHT));
            registrationHelper.register(new EyeFeatureRenderer(avatarRenderer, EyeFeatureRenderer.EyePosition.LEFT));

            registrationHelper.register(new EmissiveSkinFeatureRenderer(avatarRenderer));
        }
    }

    private static void clientTicked(Minecraft minecraft) {
        ClientLevel level = minecraft.level;
        LocalPlayer clientPlayer = minecraft.player;

        if (level == null || clientPlayer == null) return;
        if (level.tickRateManager().isFrozen() || minecraft.isPaused()) return;
        RandomSource random = level.getRandom();

        Map<String, Profile> profiles = ProfileHandler.getProfiles();
        Map<UUID, Profile> assignedProfiles = ProfileHandler.getAssignedProfiles();

        for (Profile profile : profiles.values()) {
            if (profile.maxTicks.get() > 0 && assignedProfiles.containsValue(profile)) {
                profile.data.tick();
            }
        }

        BlockPos clientPos = clientPlayer.getOnPos();
        int updateRange = CONFIG.updateRange.get();

        for (Map.Entry<UUID, Profile> entry : assignedProfiles.entrySet()) {
            UUID uuid = entry.getKey();
            Entity entity = level.getEntity(uuid);

            if (entity instanceof LivingEntity living) {
                Profile profile = entry.getValue();
                BlockPos entityPosition = living.getOnPos();

                if (profile.showEyes.get()) {
                    if (living.isInvisible() || entityPosition.distSqr(clientPos) > (updateRange * updateRange)) continue;
                    ProfileHandler.getRuntime(entity).tick(entity, random);
                }
            }
        }

        if (level.getGameTime() % 200 == 0) {
            ProfileHandler.cleanUp(level);
        }
    }

    private static void joined(ClientPacketListener listener, PacketSender sender, Minecraft minecraft) {
        ProfileHandler.walkProfiles();
        ProfileHandler.loadProfiles();
        ProfileHandler.loadCurrentProfile(minecraft.player);
    }

    private static void left(ClientPacketListener listener, Minecraft minecraft) {
        ProfileHandler.clear();
    }


    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(ClientCommands.literal("skinmatics")
                .then(ClientCommands.literal("profile")
                        .then(ClientCommands.literal("open")
                                .executes(context -> {
                                    Util.getPlatform().openPath(UtilitaryJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles"));
                                    context.getSource().sendFeedback(Component.literal("Opening profiles"));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("create")
                                .then(ClientCommands.argument("profile", StringArgumentType.string())
                                        .executes(context -> {
                                            String profileName = StringArgumentType.getString(context, "profile");
                                            Profile profile = ProfileHandler.loadProfile(profileName);
                                            profile.save();

                                            Util.getPlatform().openFile(profile.getDestination().toFile());
                                            context.getSource().sendFeedback(Component.literal(String.format("Created %s profile (%s.json) ", profileName, profileName)));
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommands.literal("load")
                                .then(ClientCommands.argument("profile", StringArgumentType.string())
                                        .executes(context -> {
                                            String profileName = StringArgumentType.getString(context, "profile");
                                            SkinmaticsConfig.INSTANCE.profile.set(profileName);
                                            ProfileHandler.loadCurrentProfile(context.getSource().getPlayer());
                                            SkinmaticsConfig.INSTANCE.save();
                                            context.getSource().sendFeedback(Component.literal(String.format("Loaded %s profile to self", profileName)));
                                            return 1;
                                        })
                                )
                        )
                        .then(ClientCommands.literal("reload")
                                .executes(context -> {
                                    ProfileHandler.walkProfiles();
                                    ProfileHandler.loadProfiles();
                                    ProfileHandler.loadCurrentProfile(context.getSource().getPlayer());
                                    context.getSource().sendFeedback(Component.literal(String.format("Reloaded %s profiles", ProfileHandler.getProfiles().size())));
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
                                            String profileName = StringArgumentType.getString(context, "profile");
                                            Profile profile = ProfileHandler.getProfile(profileName);

                                            if (profile == null) {
                                                context.getSource().sendError(Component.literal(String.format("Profile %s not found", profileName)));
                                                return 0;
                                            }

                                            Profile oldData = ProfileHandler.assignProfile(entity, profile);
                                            String entityName = entity.getName().getString();

                                            if (oldData == null) {
                                                context.getSource().sendFeedback(Component.literal(String.format("Assigned %s to %s", profileName, entityName)));
                                            } else {
                                                context.getSource().sendFeedback(Component.literal(String.format("Assigned %s to %s, previously assigned to %s", profileName, entityName, oldData.getName())));
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
                                    Profile data = ProfileHandler.unassignProfile(entity);

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
                                    Util.getPlatform().openPath(UtilitaryJsonUtils.getConfigPath().resolve(Skinmatics.ID).resolve("profiles"));
                                    context.getSource().sendFeedback(Component.literal("Opening textures"));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("reload")
                                .executes(context -> {
                                    Minecraft.getInstance().reloadResourcePacks().whenComplete((_, _) -> context.getSource().sendFeedback(Component.literal("Reloaded textures")));
                                    return 1;
                                }))
                )
        );
    }
}
