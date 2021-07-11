package snownee.minieffects.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.Rectangle2d;
import snownee.minieffects.IAreasGetter;

@Mixin(targets = "mezz.jei.plugins.vanilla.InventoryEffectRendererGuiHandler")
public class MixinInventoryEffectRendererGuiHandler {

	@Inject(method = "getGuiExtraAreas", at = @At("HEAD"), cancellable = true, remap = false)
	public void getGuiExtraAreas(DisplayEffectsScreen<?> containerScreen, CallbackInfoReturnable<List<Rectangle2d>> ci) {
		if (containerScreen instanceof IAreasGetter)
			ci.setReturnValue(((IAreasGetter) containerScreen).getAreas());
	}

}
