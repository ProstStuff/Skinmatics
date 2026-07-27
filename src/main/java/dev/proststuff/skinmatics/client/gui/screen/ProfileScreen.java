package dev.proststuff.skinmatics.client.gui.screen;

import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.gui.widget.button.NewProfileButton;
import dev.proststuff.skinmatics.client.gui.widget.button.ProfileButton;
import dev.proststuff.skinmatics.client.skinmatics.ProfileManager;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.profile.Profile;
import dev.proststuff.skinmatics.client.skinmatics.profile.ProfileMetadataType;
import dev.proststuff.skinmatics.client.skinmatics.texture.SkinmaticsAssets;
import dev.proststuff.utilitary.Utilitary;
import dev.proststuff.utilitary.api.v1.util.Either;
import dev.proststuff.utilitary.api.v1.util.Group;
import dev.proststuff.utilitary.config.v1.ConfigResult;
import dev.proststuff.utilitary.config.v1.ConfigStatus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProfileScreen extends HomeScreen {
    protected NewProfileButton newProfileButton;

    protected Group<String, String> profileGroups = new Group<>();
    protected Map<String, ProfileButton> profileButtons = new LinkedHashMap<>();
    protected List<String> problematicProfiles = new ArrayList<>();

    public ProfileScreen(Screen parent) {
        super(Component.translatable("skinmatics.profiles"), parent);
    }

    public void fillProfiles() {
        SkinmaticsConfig.load();
        ProfileManager.scan();

        children().removeIf(child -> child instanceof ProfileButton);
        profileButtons.clear();

        int contentWidth = getScrollableWidth();
        int contentHeight = getScrollableHeight();

        int horizontal = gui().getHorizontalProfiles();
        int vertical = gui().getVerticalProfiles();

        int profileWidth = contentWidth / horizontal;
        int profileHeight = contentHeight / vertical;
        int x = 0;
        int y = 0;

        for (String fileName : ProfileManager.PROFILE_FILES) {
            ProfileButton button = new ProfileButton(INSET + x * profileWidth, y * profileHeight, profileWidth, profileHeight, fileName);
            profileButtons.put(fileName, button);

            x ++;

            if (x >= horizontal) {
                x = 0;
                y ++;
            }

            addRenderableWidget(button);
            addWidget(button.customizeButton);
            addWidget(button.deleteButton);

            if (button.isClientProfile()) {
                setFocused(button);
            }
        }

        newProfileButton = new NewProfileButton(INSET + x * profileWidth, y * profileHeight, profileWidth, profileHeight);
        addRenderableWidget(newProfileButton);
        applyScroll();

        loadProfiles();
    }

    public void loadProfiles() {
        for (String fileName : ProfileManager.PROFILE_FILES) {
            if (Utilitary.CONFIG.debugLogging()) SkinmaticsMod.LOGGER.info("[SKINMATICS PROFILES] Loading {}", fileName);

            if (!problematicProfiles.contains(fileName)) {
                Either<Profile, CompletableFuture<ConfigResult<Profile, ProfileMetadataType.Metadata>>> profile = ProfileManager.getOrLoadAsync(fileName);

                profile.ifLeft((p) -> profileButtons.get(fileName).setProfile(p)).ifRight(
                        future -> future.thenAcceptAsync(result -> {
                            ConfigStatus status = result.status();

                            if (status != ConfigStatus.SUCCESS) {
                                SkinmaticsMod.LOGGER.warn("[SKINMATICS PROFILES] There is a problem when loading {}", fileName);
                                problematicProfiles.add(fileName);
                            }
                            ProfileButton button = profileButtons.get(fileName);

                            if (button != null) {
                                button.set(result);
                                if (Utilitary.CONFIG.debugLogging()) SkinmaticsMod.LOGGER.info("[SKINMATICS PROFILES] Loaded {} as {}", fileName, result.config().name());
                            }
                        }, minecraft)
                );
            }
        }
    }

    @Override
    public Identifier getScreenId() {
        return SkinmaticsAssets.PROFILES;
    }

    @Override
    public void populateContent() {
        fillProfiles();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(INSET, 0, width - INSET, height, backgroundColor());
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void onClose() {
        ProfileManager.unload(false);
        SkinmaticsConfig.save();
        super.onClose();
    }
}