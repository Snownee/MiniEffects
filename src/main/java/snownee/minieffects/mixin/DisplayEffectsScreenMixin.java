package snownee.minieffects.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.minieffects.IAreasGetter;
import snownee.minieffects.MiniEffects;
import snownee.minieffects.MiniEffectsConfig;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class DisplayEffectsScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IAreasGetter {
	public DisplayEffectsScreenMixin(T abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	private boolean expand;
	private int effects;
	private Rect2i area;
	private ItemStack iconItem = new ItemStack(Items.POTION);

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void minieffects$renderEffects(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
		minieffects$updateArea();
		if (area == null) {
			ci.cancel();
			return;
		}

		int effects = 0, bad = 0;
		LocalPlayer player = minecraft.player;
		for (MobEffectInstance effectInstance : player.getActiveEffects()) {
			++effects;
			if (!effectInstance.getEffect().isBeneficial())
				++bad;
		}

		this.effects = effects;
		int x = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
		int y = (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
		boolean expand = MiniEffectsConfig.requiresHoldingTab || area.contains(x, y);
		if (expand != this.expand) {
			this.expand = expand;
			minieffects$updateArea();
		}
		if (effects > 0 && !expand) {
			x = area.getX();
			y = area.getY();
			guiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, x, y, 0, 141, 166, 24, 24, 256, 256);
			var poseStack = guiGraphics.pose();

			if (!MiniEffectsConfig.potionItemIcon) {
				var effectsToShow = player.getActiveEffects().stream().skip(Math.max(0, effects - 4)).toList();
				var mobEffectTextures = minecraft.getMobEffectTextures();
				if (effectsToShow.size() == 1) {
					guiGraphics.blit(x + 4, y + 4, 0, 16, 16, mobEffectTextures.get(effectsToShow.get(0).getEffect()));
				} else if (effectsToShow.size() == 2) {
					guiGraphics.blit(x + 3, y + 4, 0, 10, 10, mobEffectTextures.get(effectsToShow.get(0).getEffect()));
					guiGraphics.blit(x + 3 + 8, y + 4 + 8, 0, 10, 10, mobEffectTextures.get(effectsToShow.get(1).getEffect()));
				} else if (effectsToShow.size() > 2) {
					var effectsPerLine = Mth.ceil(effectsToShow.size() / 2f);
					var effectWidth = 16 / effectsPerLine;
					for (var i1 = 0; i1 < effectsPerLine; i1++) {
						var effectInstance = effectsToShow.get(i1);
						guiGraphics.blit(x + 3 + effectWidth * i1, y + 3, 0, 8, 8, mobEffectTextures.get(effectInstance.getEffect()));
					}
					for (var i1 = 0; i1 < effectsToShow.size() - effectsPerLine; i1++) {
						var effectInstance = effectsToShow.get(i1 + effectsPerLine);
						guiGraphics.blit(x + 3 + effectWidth * i1, y + 3 + 9, 0, 8, 8, mobEffectTextures.get(effectInstance.getEffect()));
					}
				}
			} else {
				int color = player.getEntityData().get(LivingEntity.DATA_EFFECT_COLOR_ID);
				iconItem.getOrCreateTag().putInt("CustomPotionColor", color);
				guiGraphics.renderFakeItem(iconItem, x + 3, y + 4);
			}

			poseStack.pushPose();
			poseStack.translate(0, 0, 200);
			var yOffset = 0;
			if (effects - bad > 0) {
				yOffset = -10;
				String s = Integer.toString(effects - bad);
				guiGraphics.drawString(
						minecraft.font,
						s,
						x + 22 - minecraft.font.width(s),
						y + 14,
						16777215
				);
			}
			if (bad > 0) {
				String s = Integer.toString(bad);
				guiGraphics.drawString(
						minecraft.font,
						s,
						x + 22 - minecraft.font.width(s),
						y + 14 + yOffset,
						16733525
				);
			}
			poseStack.popPose();
			ci.cancel();
		}
	}

	@Unique
	private void minieffects$updateArea() {
		if (!canSeeEffects()) {
			area = null;
			return;
		}
		int left;
		boolean fullWidth;
		if (MiniEffects.isLeftSide()) {
			fullWidth = leftPos > 120;
			if (expand) {
				left = fullWidth ? leftPos - 120 - 4 : leftPos - 32 - 4;
			} else {
				left = leftPos - 20 - 8;
			}
		} else {
			left = leftPos + imageWidth + 2;
			fullWidth = (width - left) >= 120;
		}
		if (expand) {
			int height = effects > 5 ? 165 : 33 * effects;
			area = new Rect2i(left, topPos, fullWidth ? 120 : 32, height);
		} else {
			area = new Rect2i(left, topPos, 20, 20);
		}
	}

	@Override
	public List<Rect2i> getAreas() {
		if (area == null || effects == 0)
			return List.of();
		return List.of(area);
	}

	@Override
	public boolean isExpanded() {
		return expand;
	}

	@Shadow
	public abstract boolean canSeeEffects();

	@Inject(at = @At("HEAD"), method = "canSeeEffects", cancellable = true)
	private void minieffects$canSeeEffects(CallbackInfoReturnable<Boolean> ci) {
		if (MiniEffectsConfig.requiresHoldingTab && Minecraft.getInstance().options.keyInventory.key.getValue() == InputConstants.KEY_TAB) {
			ci.setReturnValue(false);
			return;
		}
		if (MiniEffectsConfig.requiresHoldingTab && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_TAB)) {
			ci.setReturnValue(false);
			return;
		}
		if (this instanceof RecipeUpdateListener listener && listener.getRecipeBookComponent().isVisible()) {
			ci.setReturnValue(false);
			return;
		}
	}
}
