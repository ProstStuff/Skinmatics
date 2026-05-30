package dev.proststuff.skinmatics.mixin.client.skin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.proststuff.skinmatics.api.client.impl.mixin.UIRendered;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
public class InventoryAvatar {
    @ModifyExpressionValue(method = "extractEntityInInventoryFollowsMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractRenderState(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;"))
    private static EntityRenderState skinmatics$markAsUI(EntityRenderState original) {
        if (original instanceof UIRendered uiRendered) {
            uiRendered.skinmatics$uiRender();
        }

        return original;
    }
}
