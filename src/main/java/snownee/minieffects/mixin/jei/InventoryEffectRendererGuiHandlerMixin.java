package snownee.minieffects.mixin.jei;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mezz.jei.library.plugins.vanilla.InventoryEffectRendererGuiHandler;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import snownee.minieffects.IAreasGetter;

@Mixin(value = InventoryEffectRendererGuiHandler.class, remap = false)
public class InventoryEffectRendererGuiHandlerMixin {

	@Inject(method = "getGuiExtraAreas", at = @At("HEAD"), cancellable = true, require = 0)
	private void getGuiExtraAreas(EffectRenderingInventoryScreen<?> containerScreen, CallbackInfoReturnable<List<Rect2i>> ci) {
		if (containerScreen instanceof IAreasGetter)
			ci.setReturnValue(((IAreasGetter) containerScreen).getAreas());
	}

}
