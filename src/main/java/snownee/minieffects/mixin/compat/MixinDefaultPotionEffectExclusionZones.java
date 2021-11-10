package snownee.minieffects.mixin.compat;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import snownee.minieffects.IAreasGetter;

@Mixin(targets = "me.shedaniel.rei.plugin.client.exclusionzones.DefaultPotionEffectExclusionZones", remap = false)
public class MixinDefaultPotionEffectExclusionZones {

	@Inject(method = "provide", at = @At("HEAD"), cancellable = true, require = 0)
	public void getGuiExtraAreas(EffectRenderingInventoryScreen<?> containerScreen, CallbackInfoReturnable<Collection<Rectangle>> ci) {
		if (containerScreen instanceof IAreasGetter)
			ci.setReturnValue(((IAreasGetter) containerScreen).getAreas().stream().map($ -> mapRect($)).collect(Collectors.toList()));
	}

	private static Rectangle mapRect(Rect2i rect) {
		return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

}
