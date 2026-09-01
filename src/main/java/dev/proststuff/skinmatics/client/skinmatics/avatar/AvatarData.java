package dev.proststuff.skinmatics.client.skinmatics.avatar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.avatar.data.*;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.manager.AvatarDataManager;
import dev.proststuff.utilitary.api.v1.util.SimpleIdentifier;
import dev.proststuff.utilitary.config.v1.ConfigCodec;
import dev.proststuff.utilitary.config.v1.ConfigManager;
import dev.proststuff.utilitary.config.v1.ConfigStatus;
import dev.proststuff.utilitary.config.v1.ConfigType;
import dev.proststuff.utilitary.config.v1.serialization.format.JsonConfigFormat;
import dev.proststuff.utilitary.config.v1.serialization.metadata.SimpleMetadataType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class AvatarData {
    public static final Codec<AvatarData> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.STRING.fieldOf("name").forGetter(AvatarData::name),
                    Codec.INT.fieldOf("maxTicks").forGetter(AvatarData::maxTicks),
                    Codec.BOOL.fieldOf("strongerEmissiveGlow").forGetter(AvatarData::strongerEmissiveGlow),
                    Skin.CODEC.fieldOf("skin").forGetter(AvatarData::skin),
                    Cape.CODEC.fieldOf("cape").forGetter(AvatarData::cape),
                    Wings.CODEC.fieldOf("wings").forGetter(AvatarData::wings),
                    Eyes.CODEC.fieldOf("eyes").forGetter(AvatarData::eyes),
                    Lights.CODEC.fieldOf("lights").forGetter(AvatarData::lights)
            ).apply(inst, AvatarData::new)
    );

    public static final ConfigType<AvatarData, SimpleMetadataType.Metadata, ?> TYPE = new ConfigType<>(
            SkinmaticsMod.of("profile"),
            1,
            ConfigCodec.of(CODEC),
            (id) -> new AvatarData(AvatarUtils.fromPathIdentifier(id)),
            SimpleMetadataType.INSTANCE,
            JsonConfigFormat.INSTANCE
    );
    
    public String name;
    public int maxTicks;
    public boolean strongerEmissiveGlow;
    private final Skin skin;
    private final Cape cape;
    private final Wings wings;
    private final Eyes eyes;
    private final Lights lights;

    public AvatarData(String name, int maxTicks, boolean strongerEmissiveGlow, Skin skin, Cape cape, Wings wings, Eyes eyes, Lights lights) {
        this.name = name;
        this.maxTicks = maxTicks;
        this.strongerEmissiveGlow = strongerEmissiveGlow;
        this.skin = skin;
        this.cape = cape;
        this.wings = wings;
        this.eyes = eyes;
        this.lights = lights;
    }

    public AvatarData(String name) {
        this(name, 0, false, new Skin(), new Cape(), new Wings(), new Eyes(), new Lights());
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return SkinmaticsConfig.getInstance().useStrongerEmissiveGlow() || strongerEmissiveGlow() ? RenderTypes.eyes(texture) : RenderTypes.entityTranslucentEmissive(texture);
    }

    public String name() {
        return name;
    }

    public int maxTicks() {
        return maxTicks;
    }

    public boolean strongerEmissiveGlow() {
        return strongerEmissiveGlow;
    }

    public Skin skin() {
        return skin;
    }

    public Cape cape() {
        return cape;
    }

    public Wings wings() {
        return wings;
    }

    public Eyes eyes() {
        return eyes;
    }

    public Lights lights() {
        return lights;
    }

    public SimpleIdentifier toPath() {
        String path = AvatarDataManager.INSTANCE.getKey(this);
        if (path == null) throw new IllegalStateException(name + " AvatarData is not registered");
        return AvatarUtils.toPathIdentifier(path);
    }

    public boolean save() {
        return ConfigManager.save(toPath(), TYPE, this) == ConfigStatus.SUCCESS;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AvatarData) obj;
        return Objects.equals(this.name, that.name) &&
                this.maxTicks == that.maxTicks &&
                this.strongerEmissiveGlow == that.strongerEmissiveGlow &&
                Objects.equals(this.skin, that.skin) &&
                Objects.equals(this.cape, that.cape) &&
                Objects.equals(this.wings, that.wings) &&
                Objects.equals(this.eyes, that.eyes) &&
                Objects.equals(this.lights, that.lights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maxTicks, strongerEmissiveGlow, skin, cape, wings, eyes, lights);
    }

    @Override
    public String toString() {
        return "Profile[" +
                "name=" + name + ", " +
                "maxTicks=" + maxTicks + ", " +
                "strongerEmissiveGlow=" + strongerEmissiveGlow + ", " +
                "skin=" + skin + ", " +
                "cape=" + cape + ", " +
                "wings=" + wings + ", " +
                "eyes=" + eyes + ", " +
                "lights=" + lights + ']';
    }
}