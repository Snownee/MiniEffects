package snownee.minieffects.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import snownee.minieffects.IAreasGetter;
import snownee.minieffects.MiniEffects;
import snownee.minieffects.MiniEffectsConfig;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class DisplayEffectsScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IAreasGetter {
	public DisplayEffectsScreenMixin(T abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	@Unique
	private boolean minieffects$expand;
	@Unique
	private int minieffects$effects;
	@Unique
	private Rect2i minieffects$area;

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void minieffects$renderEffects(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final CallbackInfo ci) {
		minieffects$updateArea();
		if (minieffects$area == null) {
			ci.cancel();
			return;
		}

		int effects = 0, bad = 0;
		LocalPlayer player = minecraft.player;
		for (MobEffectInstance effectInstance : player.getActiveEffects()) {
			++effects;
			if (!effectInstance.getEffect().value().isBeneficial()) {
				++bad;
			}
		}

		this.minieffects$effects = effects;
		int x = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
		int y = (int) (
				minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
		boolean expand = MiniEffectsConfig.requiresHoldingTab || minieffects$area.contains(x, y);
		if (expand != this.minieffects$expand) {
			this.minieffects$expand = expand;
			minieffects$updateArea();
		}
		if (effects > 0 && !expand) {
			x = minieffects$area.getX();
			y = minieffects$area.getY();
			guiGraphics.blitSprite(GuiAccess.EFFECT_BACKGROUND_SPRITE(), x, y, 24,24);
			var poseStack = guiGraphics.pose();

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
			minieffects$area = null;
			return;
		}
		int left;
		boolean fullWidth;
		if (MiniEffects.isLeftSide()) {
			fullWidth = leftPos > 120;
			if (minieffects$expand) {
				left = fullWidth ? leftPos - 120 - 4 : leftPos - 32 - 4;
			} else {
				left = leftPos - 20 - 8;
			}
		} else {
			left = leftPos + imageWidth + 2;
			fullWidth = (width - left) >= 120;
		}
		if (minieffects$expand) {
			int height = minieffects$effects > 5 ? 165 : 33 * minieffects$effects;
			minieffects$area = new Rect2i(left, topPos, fullWidth ? 120 : 32, height);
		} else {
			minieffects$area = new Rect2i(left, topPos, 20, 20);
		}
	}

	@Unique
	@Override
	public List<Rect2i> minieffects$getAreas() {
		if (minieffects$area == null || minieffects$effects == 0) {
			return List.of();
		}
		return List.of(minieffects$area);
	}

	@Unique
	@Override
	public boolean minieffects$isExpanded() {
		return minieffects$expand;
	}

	@Shadow
	public abstract boolean canSeeEffects();

	@Shadow
	@Final
	private static ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE;

	@Inject(at = @At("HEAD"), method = "canSeeEffects", cancellable = true)
	private void minieffects$canSeeEffects(CallbackInfoReturnable<Boolean> ci) {
		if (MiniEffectsConfig.requiresHoldingTab && Minecraft.getInstance().options.keyInventory.key.getValue() == InputConstants.KEY_TAB) {
			ci.setReturnValue(false);
			return;
		}
		if (MiniEffectsConfig.requiresHoldingTab && !InputConstants.isKeyDown(
				Minecraft.getInstance().getWindow().getWindow(),
				InputConstants.KEY_TAB)) {
			ci.setReturnValue(false);
			return;
		}
		if (this instanceof RecipeUpdateListener listener && listener.getRecipeBookComponent().isVisible()) {
			ci.setReturnValue(false);
		}
	}
}
