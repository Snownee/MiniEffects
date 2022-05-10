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

@Mixin(value = EffectRenderingInventoryScreen.class, priority = 900)
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
	private int minieffects$renderEffects0(int i) {
		if (MiniEffects.isLeftSide()) {
			if (leftPos > 120) {
				return leftPos - 120;
			} else {
				return leftPos - 32;
			}
		} else {
			return i;
		}
	}

	@ModifyVariable(method = "renderEffects", at = @At(value = "STORE", ordinal = 0), index = 5)
	private int minieffects$renderEffects1(int i) {
		if (MiniEffects.isLeftSide()) {
			return leftPos - 2;
		} else {
			return i;
		}
	}

}
