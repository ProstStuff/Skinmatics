package dev.proststuff.skinmatics.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.manager.AvatarProfileManager;
import dev.proststuff.skinmatics.manager.AvatarTrackerManager;
import dev.proststuff.utilitary.config.ConfigType;
import dev.proststuff.utilitary.config.serialization.ConfigParser;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationResult;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationStatus;
import dev.proststuff.utilitary.config.serialization.format.Json5ConfigFormat;
import dev.proststuff.utilitary.config.serialization.metadata.SimpleMetadataType;
import dev.proststuff.utilitary.config.serialization.Comment;
import dev.proststuff.utilitary.util.SimpleIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class SkinmaticsConfig {
    private static final SimpleIdentifier ID = Skinmatics.ofSimple("config");
    private static final SkinmaticsConfig INSTANCE = new SkinmaticsConfig();
    private static final Codec<SkinmaticsConfig> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.STRING.fieldOf("avatar").forGetter(SkinmaticsConfig::getAvatar),
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("updateRange").forGetter(SkinmaticsConfig::getUpdateRange),
                    Codec.BOOL.fieldOf("forceStrongerEmissive").forGetter(SkinmaticsConfig::useStrongerEmissiveGlow),
                    GUI.CODEC.fieldOf("gui").forGetter(SkinmaticsConfig::getGui),
                    PaperDoll.CODEC.fieldOf("paperDoll").forGetter(SkinmaticsConfig::getPaperDoll)
            ).apply(inst, SkinmaticsConfig::apply)
    );

    private static final ConfigType<SkinmaticsConfig, SimpleMetadataType.Metadata> TYPE = new ConfigType<>(
            ID.asNormalizedIdentifier(),
            0,
            ConfigType.fileCodec(SimpleMetadataType.CODEC, CODEC),
            (_) -> INSTANCE,
            SimpleMetadataType.INSTANCE,
            Json5ConfigFormat.INSTANCE,
            Comment.of("Skinmatics Configuration")
    );

    static {
        TYPE.comments()
                .addComment("avatar", "Selected avatar to be used (string, relative to <minecraft>/config/skinmatics/avatars)")
                .addComment("updateRange", "Update range of active avatar in blocks (int, min 0)")
                .addComment("forceStrongerEmissive", "Force avatars to have strongerEmissive true even though it's off (boolean)")
                .addComment("gui", Comment.of("Configure GUI")
                        .addComment("background", "The background color of Skinmatics GUIs (string, hex color code)")
                        .addComment("foreground", "The foreground color of Skinmatics GUIs\nUsually for text color, hover color, and ui texture color (string, hex color code)")
                        .addComment("accent", "The accent color of Skinmatics GUIs\nUsually for focused button color (string, hex color code)")
                        .addComment("horizontalProfiles", "Amount of visible avatar cards on a screen horizontally (int)")
                        .addComment("verticalProfiles", "Amount of visible avatar cards on a screen vertically (int)")
                )
                .addComment("paperDoll", Comment.of("Configure paper doll")
                        .addComment("visible", "Enable paper doll (boolean)")
                        .addComment("isAlwaysVisible", "Whether paper doll should always be visible (boolean)")
                        .addComment("visibleTicks", "The amount of ticks the paper doll visible for (boolean)\nDisabled if isAlwaysVisible is true")
                        .addComment("hideEquipments", "Whether paper doll should hide equipments (items and armors) (boolean)")
                        .addComment("scale", "The scale of the paper doll (float)")
                        .addComment("screenAlignment", "The screen alignment of the paper doll (float)")
                        .addComment("modelAlignment", "The model (anchor) alignment of the paper doll (float)")
                        .addComment("offset", "The offset of the paper doll (in pixels) (float)")
                )
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
            AvatarTrackerManager.INSTANCE.register(player, AvatarProfileManager.INSTANCE.get(avatar));
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

    public static ConfigSerializationResult<SkinmaticsConfig, SimpleMetadataType.Metadata> load() {
        return ConfigParser.read(TYPE, ID);
    }

    public static ConfigSerializationStatus save() {
        return ConfigParser.write(TYPE, ID, SkinmaticsConfig.INSTANCE);
    }
}