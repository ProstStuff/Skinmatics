package dev.proststuff.skinmatics.mixin.client.skin;

import dev.proststuff.skinmatics.api.impl.AvatarPreviewState;
import dev.proststuff.skinmatics.client.gui.element.avatar.BodyPart;
import dev.proststuff.skinmatics.client.gui.element.avatar.AvatarMinimap;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class SkinmaticsPlayerModel {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At(value = "TAIL"))
    public void skinmatics$setupAnim(AvatarRenderState state, CallbackInfo ci) {
        AvatarPreviewState previewState = (AvatarPreviewState) state;

        // TODO: Also control Equipment(s) visibility
        if (previewState.skinmatics$isPreviewing()) {
            PlayerModel model = (PlayerModel) (Object) this;
            model.head.visible = AvatarMinimap.isVisible(BodyPart.HEAD);
            model.hat.visible = model.head.visible;
            state.showHat = model.hat.visible;

            model.body.visible = AvatarMinimap.isVisible(BodyPart.TORSO);
            model.jacket.visible = model.body.visible;
            state.showJacket = model.jacket.visible;

            model.rightArm.visible = AvatarMinimap.isVisible(BodyPart.RIGHT_ARM);
            model.rightSleeve.visible = model.rightArm.visible;
            state.showRightSleeve = model.rightArm.visible;

            model.leftArm.visible = AvatarMinimap.isVisible(BodyPart.LEFT_ARM);
            model.leftSleeve.visible = model.leftArm.visible;
            state.showLeftSleeve = model.leftArm.visible;

            model.rightLeg.visible = AvatarMinimap.isVisible(BodyPart.RIGHT_LEG);
            model.rightPants.visible = model.rightLeg.visible;
            state.showRightPants = model.rightPants.visible;

            model.leftLeg.visible = AvatarMinimap.isVisible(BodyPart.LEFT_LEG);
            model.leftPants.visible = model.leftLeg.visible;
            state.showLeftPants = model.leftPants.visible;
        }
    }
}