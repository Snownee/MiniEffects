package snownee.minieffects.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public interface GuiAccess {
	@Accessor("EFFECT_BACKGROUND_SPRITE")
	static ResourceLocation EFFECT_BACKGROUND_SPRITE() {
		throw new AssertionError();
	}
}
