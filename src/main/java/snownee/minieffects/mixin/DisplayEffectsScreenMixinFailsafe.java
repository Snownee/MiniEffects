package snownee.minieffects.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import snownee.minieffects.MiniEffects;

// trying to avoid conflicts with EffectsLeft (https://www.curseforge.com/minecraft/mc-mods/effectsleft)
@Mixin(value = EffectRenderingInventoryScreen.class, priority = 1100)
public abstract class DisplayEffectsScreenMixinFailsafe<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	public DisplayEffectsScreenMixinFailsafe(T abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	@Inject(at = @At("HEAD"), method = "canSeeEffects", cancellable = true)
	private void minieffects$canSeeEffects(CallbackInfoReturnable<Boolean> ci) {
		if (MiniEffects.isLeftSide()) {
			ci.setReturnValue(leftPos >= 36);
		}
	}

	@ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0), index = 4)
	private int minieffects$renderEffectsK(int k) {
		if (MiniEffects.isLeftSide()) {
			if (leftPos > 120) {
				return leftPos - 120;
			} else {
				return leftPos - 32;
			}
		} else {
			return k;
		}
	}

	@ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0), index = 5)
	private int minieffects$renderEffectsL(int l) {
		if (MiniEffects.isLeftSide()) {
			return leftPos - 2;
		} else {
			return l;
		}
	}

	// prevent full width mode from being modified by JEI
	@ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0), index = 7)
	private boolean minieffects$renderEffectsBl(boolean bl) {
		int l;
		if (MiniEffects.isLeftSide()) {
			l = leftPos - 2;
		} else {
			int k = leftPos + imageWidth + 2;
			l = width - k;
		}
		return l >= 120;
	}

}
