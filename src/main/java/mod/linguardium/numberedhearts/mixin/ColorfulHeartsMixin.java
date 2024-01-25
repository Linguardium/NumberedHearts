package mod.linguardium.numberedhearts.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.linguardium.numberedhearts.NumberedHearts;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.heart.Heart;
import terrails.colorfulhearts.heart.HeartType;
import terrails.colorfulhearts.render.HeartRenderer;

@Pseudo
@Mixin(HeartRenderer.class)
public class ColorfulHeartsMixin {

    @ModifyVariable(method="renderPlayerHearts", ordinal = 1, at=@At("STORE"))
    private boolean modifyLinesToOneIfNotRenderingHearts(boolean original) {
        if (!NumberedHearts.shouldRenderHearts()) return true;
        return original;
    }

    @WrapWithCondition(method="renderPlayerHearts",at=@At(value="INVOKE",target = "Lterrails/colorfulhearts/heart/Heart;draw(Lnet/minecraft/client/util/math/MatrixStack;IIZZLterrails/colorfulhearts/heart/HeartType;)V"))
    private boolean hideColorfulHeartsHearts(Heart instance, MatrixStack poseStack, int xPos, int yPos, boolean blinkBackground, boolean blinkHeart, HeartType type) {
        return NumberedHearts.shouldRenderHearts();
    }
}
