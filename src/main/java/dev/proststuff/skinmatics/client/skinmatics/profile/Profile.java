package dev.proststuff.skinmatics.client.skinmatics.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.proststuff.skinmatics.SkinmaticsMod;
import dev.proststuff.skinmatics.client.skinmatics.config.SkinmaticsConfig;
import dev.proststuff.skinmatics.client.skinmatics.profile.data.*;
import dev.proststuff.utilitary.api.v1.client.renderer.CustomRenderTypes;
import dev.proststuff.utilitary.config.v1.ConfigCodec;
import dev.proststuff.utilitary.config.v1.ConfigType;
import dev.proststuff.utilitary.config.v1.serialization.format.JsonConfigFormat;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class Profile {
    public static final Codec<Profile> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    Codec.STRING.fieldOf("name").forGetter(Profile::name),
                    Codec.INT.fieldOf("maxTicks").forGetter(Profile::maxTicks),
                    Codec.BOOL.fieldOf("strongerEmissiveGlow").forGetter(Profile::strongerEmissiveGlow),
                    Skin.CODEC.fieldOf("skin").forGetter(Profile::skin),
                    Cape.CODEC.fieldOf("cape").forGetter(Profile::cape),
                    Wings.CODEC.fieldOf("wings").forGetter(Profile::wings),
                    Eyes.CODEC.fieldOf("eyes").forGetter(Profile::eyes),
                    Lights.CODEC.fieldOf("lights").forGetter(Profile::lights)
            ).apply(inst, Profile::new)
    );

    public static final ConfigType<Profile, ProfileMetadataType.Metadata, ?> TYPE = new ConfigType<>(
            SkinmaticsMod.of("profile"),
            1,
            ConfigCodec.of(CODEC),
            (id) -> new Profile(ProfileUtils.fromSimpleIdentifier(id)),
            new ProfileMetadataType(),
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

    public Profile(String name, int maxTicks, boolean strongerEmissiveGlow, Skin skin, Cape cape, Wings wings, Eyes eyes, Lights lights) {
        this.name = name;
        this.maxTicks = maxTicks;
        this.strongerEmissiveGlow = strongerEmissiveGlow;
        this.skin = skin;
        this.cape = cape;
        this.wings = wings;
        this.eyes = eyes;
        this.lights = lights;
    }

    public Profile(String name) {
        this(name, 0, false, new Skin(), new Cape(), new Wings(), new Eyes(), new Lights());
    }

    public RenderType getEmissiveRenderType(Identifier texture) {
        return SkinmaticsConfig.getInstance().useStrongerEmissiveGlow() || strongerEmissiveGlow() ? CustomRenderTypes.eyesNoCull(texture) : RenderTypes.entityTranslucentEmissive(texture);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Profile) obj;
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