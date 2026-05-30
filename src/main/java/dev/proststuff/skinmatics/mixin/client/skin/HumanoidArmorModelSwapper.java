package dev.proststuff.skinmatics.mixin.client.skin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.proststuff.skinmatics.api.client.Profile;
import dev.proststuff.skinmatics.api.client.impl.mixin.PlayerModelGetter;
import dev.proststuff.skinmatics.api.client.impl.mixin.PlayerModelSetter;
import dev.proststuff.skinmatics.api.client.impl.mixin.ProfileHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorModelSwapper implements PlayerModelSetter {
    @Shadow
    @Final
    @Mutable
    private ArmorModelSet<? extends HumanoidModel<? extends HumanoidRenderState>> modelSet;

    @Shadow
    @Final
    @Mutable
    private ArmorModelSet<? extends HumanoidModel<? extends HumanoidRenderState>> babyModelSet;

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"))
    public void skinmatics$swapModelSubmit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        if (state instanceof ProfileHolder profileHolder && state instanceof PlayerModelGetter modelGetter) {
            Profile profile = profileHolder.skinmatics$getProfile();

            if (profile != null) {
                skinmatics$setCurrentArmor(modelGetter.skinmatics$getArmorModel(profile));
            }
        }
    }

    @Inject(method = "getArmorModel", at = @At("RETURN"), cancellable = true)
    public void skinmatics$swapModel(HumanoidRenderState state, EquipmentSlot slot, CallbackInfoReturnable<HumanoidModel<?>> cir) {
        if (state instanceof ProfileHolder profileHolder && state instanceof PlayerModelGetter modelGetter) {
            Profile profile = profileHolder.skinmatics$getProfile();

            if (profile != null) {
                cir.setReturnValue(modelGetter.skinmatics$getArmorModel(profile).get(slot));
            }
        }
    }

    @Override
    public void skinmatics$setWide(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setSlim(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setCurrent(PlayerModel model) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setWideArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setSlimArmor(ArmorModelSet<PlayerModel> modelSet) {
        throw new UnsupportedOperationException("Unimplemented operation");
    }

    @Override
    public void skinmatics$setCurrentArmor(ArmorModelSet<PlayerModel> modelSet) {
        this.modelSet = modelSet != null ? modelSet : this.modelSet;
        this.babyModelSet = this.modelSet;
    }
}
