package dev.proststuff.skinmatics.client.skinmatics.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import dev.proststuff.utilitary.config.v1.*;
import dev.proststuff.utilitary.config.v1.serialization.Comment;
import dev.proststuff.utilitary.config.v1.serialization.format.Json5ConfigFormat;
import dev.proststuff.utilitary.config.v1.serialization.metadata.SimpleMetadataType;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SkinmaticsConfig {
    private static final SimpleIdentifier ID = SkinmaticsMod.ofSimple("config");
    private static final SkinmaticsConfig INSTANCE = new SkinmaticsConfig();
    private static final Codec<SkinmaticsConfig> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.STRING.fieldOf("profile").forGetter(SkinmaticsConfig::getProfile),
                    Codec.INT.fieldOf("updateRange").forGetter(SkinmaticsConfig::getUpdateRange),
                    Codec.BOOL.fieldOf("forceStrongerEmissiveGlow").forGetter(SkinmaticsConfig::useStrongerEmissiveGlow),
                    GUI.CODEC.fieldOf("gui").forGetter(SkinmaticsConfig::getGui),
                    PaperDoll.CODEC.fieldOf("paperDoll").forGetter(SkinmaticsConfig::getPaperDoll),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(LinkedHashMap::new, (map) -> map)
                            .fieldOf("autoAssigns").forGetter(SkinmaticsConfig::getAutoAssigns)
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
                .addComment("profile", "Selected profile to be used")
                .addComment("updateRange", "Update range of profiled entity in blocks")
                .addComment("forceStrongerEmissiveGlow", "Force emissive textures to use a stronger emissive glow")
                .addComment("gui", Comment.of("Configure GUI")
                        .addComment("background", "The background color of Skinmatics GUIs")
                        .addComment("foreground", "The foreground color of Skinmatics GUIs\nUsually for text color, hover color, and icon color")
                        .addComment("accent", "The accent color of Skinmatics GUIs\nUsually for borders or focused button color")
                        .addComment("horizontalProfiles", "Amount of visible profile on a screen horizontally")
                        .addComment("verticalProfiles", "Amount of visible profile on a screen vertically")
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
                .addComment("autoAssigns", "Automatically assign listed UUID with associated profile\nListed as <UUID>: <Profile>");
    }

    private @Nullable String profile;
    private int updateRange = 64;
    private boolean useStrongerEmissiveGlow = false;
    private GUI gui = GUI.INSTANCE;
    private PaperDoll paperDoll = PaperDoll.INSTANCE;
    private final LinkedHashMap<String, String> autoAssigns = new LinkedHashMap<>();

    private SkinmaticsConfig() {
        this.profile = Minecraft.getInstance().getGameProfile().name();
    }

    public void setProfile(@NonNull String profile) {
        this.profile = profile;
    }

    public void setUpdateRange(int updateRange) {
        this.updateRange = updateRange;
    }

    public void setUseStrongerEmissiveGlow(boolean useStrongerEmissiveGlow) {
        this.useStrongerEmissiveGlow = useStrongerEmissiveGlow;
    }

    public @NonNull String getProfile() {
        return profile == null ? Minecraft.getInstance().getGameProfile().name() : profile;
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

    public LinkedHashMap<String, String> getAutoAssigns() {
        return autoAssigns;
    }

    private static SkinmaticsConfig apply(String profile, int updateRange, boolean forceStrongerEmissiveGlow, GUI gui, PaperDoll paperDoll, Map<String, String> autoAssigns) {
        INSTANCE.profile = profile;
        INSTANCE.updateRange = updateRange;
        INSTANCE.useStrongerEmissiveGlow = forceStrongerEmissiveGlow;
        INSTANCE.gui = gui;
        INSTANCE.paperDoll = paperDoll;
        INSTANCE.autoAssigns.clear();
        INSTANCE.autoAssigns.putAll(autoAssigns);

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
