package dev.proststuff.skinmatics.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import dev.proststuff.skinmatics.Skinmatics;
import dev.proststuff.skinmatics.avatar.manager.AvatarProfileManager;
import dev.proststuff.skinmatics.avatar.manager.AvatarTrackerManager;
import dev.proststuff.utilitary.config.ConfigConstants;
import dev.proststuff.utilitary.config.ConfigType;
import dev.proststuff.utilitary.config.serialization.Comment;
import dev.proststuff.utilitary.config.serialization.ConfigParser;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationResult;
import dev.proststuff.utilitary.config.serialization.ConfigSerializationStatus;
import dev.proststuff.utilitary.config.serialization.format.Json5ConfigFormat;
import dev.proststuff.utilitary.config.serialization.metadata.SimpleMetadataType;
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
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("unloadedTickCount").forGetter(SkinmaticsConfig::getUnloadedEntityTimeout),
                    Codec.BOOL.fieldOf("forceStrongerEmissive").forGetter(SkinmaticsConfig::useStrongerEmissiveGlow),
                    GUI.CODEC.fieldOf("gui").forGetter(SkinmaticsConfig::getGui),
                    PaperDoll.CODEC.fieldOf("paperDoll").forGetter(SkinmaticsConfig::getPaperDoll)
            ).apply(inst, SkinmaticsConfig::apply)
    );

    private static final ConfigType<SkinmaticsConfig, SimpleMetadataType.Metadata> TYPE = new ConfigType<>(
            ID.asNormalizedIdentifier(),
            1,
            ConfigType.fileCodec(SimpleMetadataType.CODEC, CODEC),
            (_) -> INSTANCE,
            SimpleMetadataType.INSTANCE,
            Json5ConfigFormat.INSTANCE,
            Comment.of("Skinmatics Configuration")
    );

    static {
        TYPE.comments()
                //.addComment("avatar", "Selected avatar to be used (string, relative to <minecraft>/config/skinmatics/avatars)")
                .addComment("updateRange", "Avatar ticking range from the client's player")
                .addComment("unloadedTickCount", "How long should an unloaded entity have their tracker removed")
                .addComment("forceStrongerEmissive", "Force avatar features to use stronger emissive glow")
                .addComment("gui", Comment.of()
                        //.addComment("background", "The background color of Skinmatics GUIs")
                        //.addComment("foreground", "The foreground color of Skinmatics GUIs\nUsually for text, hovering background, and UI texture color")
                        //.addComment("accent", "The accent color of Skinmatics GUIs")
                        .addComment("horizontalProfiles", "Amount of visible avatar cards on a screen horizontally")
                        .addComment("verticalProfiles", "Amount of visible avatar cards on a screen vertically")
                )
                .addComment("paperDoll", Comment.of()
                        .addComment("enabled", "Enable paper doll")
                        .addComment("alwaysVisible", "Whether paper doll should always be visible")
                        .addComment("visibleTicks", "How long should the paper doll be visible on idle\nDisabled if isAlwaysVisible is true")
                        .addComment("hideEquipments", "Whether paper doll should have their equipments (items and armors) hidden")
                        .addComment("scale", "The scale of the paper doll")
                        .addComment("screenAlignment", "The screen alignment of the paper doll")
                        .addComment("modelAlignment", "The model anchor/pivot of the paper doll")
                        .addComment("offset", "The offset of the paper doll (in pixels)")
                )
        ;

        Json5ConfigFormat.FIXERS.put(TYPE, (fileId, rootElement) -> {
            Json5Element metadata = rootElement.getAsJson5Object().get(ConfigConstants.METADATA_KEY);
            if (metadata.isJson5Primitive() && metadata.getAsInt() == TYPE.version()) return rootElement;

            Json5Object data = rootElement.getAsJson5Object().get(ConfigConstants.DATA_KEY).getAsJson5Object();
            Json5Object paperDoll = data.getAsJson5Object("paperDoll");

            if (data.has("unloadedTickCount")) {
                data.addProperty("unloadedTickCount", 1200);
            }

            if (paperDoll.has("visible")) {
                paperDoll.add("enabled", paperDoll.remove("visible"));
            }

            if (paperDoll.has("isAlwaysVisible")) {
                paperDoll.add("alwaysVisible", paperDoll.remove("isAlwaysVisible"));
            }

            return rootElement;
        });
    }

    private @Nullable String avatar;
    private int updateRange = 128;
    private int unloadedEntityTimeout = 1200;
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

    public @NonNull String getAvatar() {
        return avatar == null ? Minecraft.getInstance().getGameProfile().name() : avatar;
    }

    public int getUnloadedEntityTimeout() {
        return unloadedEntityTimeout;
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

    private static SkinmaticsConfig apply(String profile, int updateRange, int unloadedTickCount, boolean forceStrongerEmissiveGlow, GUI gui, PaperDoll paperDoll) {
        INSTANCE.avatar = profile;
        INSTANCE.updateRange = updateRange;
        INSTANCE.unloadedEntityTimeout = unloadedTickCount;
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