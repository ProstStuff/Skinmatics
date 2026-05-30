package dev.proststuff.skinmatics.mixin.client.ui;

import dev.proststuff.skinmatics.client.gui.customization.avatar.BodyPart;
import dev.proststuff.skinmatics.client.gui.customization.avatar.ModelPartHider;
import dev.proststuff.skinmatics.api.client.impl.mixin.EntityPreviewer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PreviewModelPartHider {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At(value = "TAIL"))
    public void skinmatics$setupAnim(AvatarRenderState state, CallbackInfo ci) {
        EntityPreviewer previewState = (EntityPreviewer) state;

        if (previewState.skinmatics$isPreviewing()) {
            PlayerModel model = (PlayerModel) (Object) this;
            model.head.visible = ModelPartHider.isVisible(BodyPart.HEAD);
            model.hat.visible = model.head.visible;
            state.showHat = model.hat.visible;

            model.body.visible = ModelPartHider.isVisible(BodyPart.TORSO);
            model.jacket.visible = model.body.visible;
            state.showJacket = model.jacket.visible;

            model.rightArm.visible = ModelPartHider.isVisible(BodyPart.RIGHT_ARM);
            model.rightSleeve.visible = model.rightArm.visible;
            state.showRightSleeve = model.rightArm.visible;

            model.leftArm.visible = ModelPartHider.isVisible(BodyPart.LEFT_ARM);
            model.leftSleeve.visible = model.leftArm.visible;
            state.showLeftSleeve = model.leftArm.visible;

            model.rightLeg.visible = ModelPartHider.isVisible(BodyPart.RIGHT_LEG);
            model.rightPants.visible = model.rightLeg.visible;
            state.showRightPants = model.rightPants.visible;

            model.leftLeg.visible = ModelPartHider.isVisible(BodyPart.LEFT_LEG);
            model.leftPants.visible = model.leftLeg.visible;
            state.showLeftPants = model.leftPants.visible;
        }
    }
}