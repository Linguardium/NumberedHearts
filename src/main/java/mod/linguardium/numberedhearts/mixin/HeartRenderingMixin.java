package mod.linguardium.numberedhearts.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.linguardium.numberedhearts.NumberedHearts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mod.linguardium.numberedhearts.NumberedHearts.renderHealth;

@Mixin(InGameHud.class)
public class HeartRenderingMixin {

	@ModifyVariable(method="renderHealthBar",argsOnly = true, ordinal = 2, at=@At("LOAD"))
	private int modifyLinesToOneIfNotRenderingHearts(int original) {
		if (!NumberedHearts.shouldRenderHearts()) return 1;
		return original;
	}

	@WrapWithCondition(at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"), method = "drawHeart")
	private boolean shouldRenderHearts(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height) {
		return NumberedHearts.shouldRenderHearts();
	}

	@Inject(at = @At("RETURN"), method = "renderHealthBar")
	private void renderHealthText(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
		renderHealth(context,x,y,player,lastHealth,(int)Math.floor(maxHealth),absorption);
	}
}