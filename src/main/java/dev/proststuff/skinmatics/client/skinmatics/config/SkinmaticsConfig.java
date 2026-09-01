package dev.proststuff.skinmatics.client.skinmatics.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarDataManager;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarTrackerManager;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import dev.proststuff.utilitary.config.v1.*;
import dev.proststuff.utilitary.config.v1.serialization.Comment;
import dev.proststuff.utilitary.config.v1.serialization.format.Json5ConfigFormat;
import dev.proststuff.utilitary.config.v1.serialization.metadata.SimpleMetadataType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class SkinmaticsConfig {
    private static final SimpleIdentifier ID = SkinmaticsMod.ofSimple("config");
    private static final SkinmaticsConfig INSTANCE = new SkinmaticsConfig();
    private static final Codec<SkinmaticsConfig> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.STRING.fieldOf("avatar").forGetter(SkinmaticsConfig::getAvatar),
                    Codec.INT.fieldOf("updateRange").forGetter(SkinmaticsConfig::getUpdateRange),
                    Codec.BOOL.fieldOf("forceStrongerEmissiveGlow").forGetter(SkinmaticsConfig::useStrongerEmissiveGlow),
                    GUI.CODEC.fieldOf("gui").forGetter(SkinmaticsConfig::getGui),
                    PaperDoll.CODEC.fieldOf("paperDoll").forGetter(SkinmaticsConfig::getPaperDoll)
            ).apply(inst, SkinmaticsConfig::apply)
    );

    private static final ConfigType<SkinmaticsConfig, SimpleMetadataType.Metadata, Json5ConfigFormat.FormatSettings> TYPE = new ConfigType<>(
            ID.asNormalizedIdentifier(),
            0,
            ConfigCodec.of(CODEC),
            (_) -> INSTANCE,
            SimpleMetadataType.INSTANCE,
            Json5ConfigFormat.INSTANCE,
            Comment.of("Skinmatics Configuration")
    );

    static {
        TYPE.comments()
                .addComment("avatar", "Selected avatar to be used")
                .addComment("updateRange", "Update range of profiled entity in blocks")
                .addComment("forceStrongerEmissiveGlow", "Force emissive textures to use a stronger emissive glow")
                .addComment("gui", Comment.of("Configure GUI")
                        .addComment("background", "The background color of Skinmatics GUIs")
                        .addComment("foreground", "The foreground color of Skinmatics GUIs\nUsually for text color, hover color, and texture color")
                        .addComment("accent", "The accent color of Skinmatics GUIs\nUsually for borders or focused button color")
                        .addComment("horizontalProfiles", "Amount of visible avatar on a screen horizontally")
                        .addComment("verticalProfiles", "Amount of visible avatar on a screen vertically")
                )
                .addComment("paperDoll", Comment.of("Configure paper doll")
                        .addComment("visible", "Enable paper doll")
                        .addComment("isAlwaysVisible", "Whether paper doll should always be visible")
                        .addComment("visibleTicks", "The amount of ticks the paper doll visible for\nDisabled if isAlwaysVisible is true")
                        .addComment("hideEquipments", "Whether paper doll should hide equipments (items and armors)")
                        .addComment("scale", "The scale of the paper doll")
                        .addComment("screenAlignment", "The screen alignment of the paper doll")
                        .addComment("modelAlignment", "The model alignment of the paper doll")
                        .addComment("offset", "The offset of the paper doll in pixels")
                )
                .addComment("autoAssigns", "Automatically assign listed UUID with associated avatar\nListed as <UUID>: <Profile>")
                .addComment("flags", "Skinmatics persistent state flags for internal use\nUsually for marking stuffs that are meant to be shown once (e.g. changelogs, legacy avatar detection, tutorials, etc.)\nUse with cautions.")
        ;
    }

    private @Nullable String avatar;
    private int updateRange = 64;
    private boolean useStrongerEmissiveGlow = false;
    private GUI gui = GUI.INSTANCE;
    private PaperDoll paperDoll = PaperDoll.INSTANCE;

    private SkinmaticsConfig() {
        this.avatar = "";
    }

    public void setAvatar(@NonNull String avatar) {
        this.avatar = avatar;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            AvatarTrackerManager.INSTANCE.register(player, AvatarDataManager.INSTANCE.get(avatar));
        }
    }

    public void setUpdateRange(int updateRange) {
        this.updateRange = updateRange;
    }

    public void setUseStrongerEmissiveGlow(boolean useStrongerEmissiveGlow) {
        this.useStrongerEmissiveGlow = useStrongerEmissiveGlow;
    }

    public @NonNull String getAvatar() {
        return avatar == null ? Minecraft.getInstance().getGameProfile().name() : avatar;
    }

    public int getUpdateRange() {
        return updateRange;
    }

    public boolean useStrongerEmissiveGlow() {
        return useStrongerEmissiveGlow;
    }

    public GUI getGui() {
        return gui;
    }

    public PaperDoll getPaperDoll() {
        return paperDoll;
    }

    private static SkinmaticsConfig apply(String profile, int updateRange, boolean forceStrongerEmissiveGlow, GUI gui, PaperDoll paperDoll) {
        INSTANCE.avatar = profile;
        INSTANCE.updateRange = updateRange;
        INSTANCE.useStrongerEmissiveGlow = forceStrongerEmissiveGlow;
        INSTANCE.gui = gui;
        INSTANCE.paperDoll = paperDoll;

        return INSTANCE;
    }

    public static SkinmaticsConfig getInstance() {
        return INSTANCE;
    }

    public static ConfigResult<SkinmaticsConfig, SimpleMetadataType.Metadata> load() {
        return ConfigManager.load(ID, TYPE);
    }

    public static ConfigStatus save() {
        return ConfigManager.save(ID, TYPE, INSTANCE);
    }
}