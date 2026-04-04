package dev.proststuff.skinmatics.mixin.client.ui;

import dev.proststuff.skinmatics.SkinmaticsClient;
import dev.proststuff.skinmatics.client.gui.SkinmaticsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public class OptionSkinMixins {
    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;createButton(Lnet/minecraft/text/Text;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/widget/ButtonWidget;"))
    private void skinmatics$createButton(Args args) {
        OptionsScreen parent =  (OptionsScreen)(Object)this;
        Text text = args.get(0);

        if (text.equals(Text.translatable("options.skinCustomisation")) && SkinmaticsClient.CONFIG.openSkinmaticsInsteadOfSkinCustomization) {
            Supplier<Screen> supplier = () -> new SkinmaticsScreen(parent);
            args.set(1, supplier);
        }
    }
}
