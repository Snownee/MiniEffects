package snownee.minieffects.mixin;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.minieffects.IAreasGetter;
import snownee.minieffects.MiniEffects;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class DisplayEffectsScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IAreasGetter {
	public DisplayEffectsScreenMixin(T abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	private boolean expand;
	private int effects;
	private Rect2i area;
	private ItemStack iconItem = new ItemStack(Items.POTION);
	//	private boolean firstTick = true;

	// handle resize
	//	@Inject(method = "init", at = @At("TAIL"))
	//	private void minieffects$init(CallbackInfo ci) {
	//		firstTick = true;
	//	}

	@Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
	private void minieffects$renderEffects(PoseStack matrixStack, int i, int j, CallbackInfo ci) {
		//		if (firstTick) {
		updateArea();
		if (area == null) {
			return;
		}
		//			firstTick = false;
		//		}

		int effects = 0, bad = 0;
		LocalPlayer player = minecraft.player;
		for (MobEffectInstance effectInstance : player.getActiveEffects()) {
			++effects;
			if (!effectInstance.getEffect().isBeneficial())
				++bad;
		}

		//		if (this.effects != effects) {
		this.effects = effects;
		//			if (effects == 0 || expand) {
		//				updateArea();
		//			}
		//		}
		int x = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
		int y = (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
		boolean expand = area.contains(x, y);
		if (expand != this.expand) {
			this.expand = expand;
			//			updateArea();
		}
		if (effects > 0 && !expand) {
			RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			x = area.getX();
			y = area.getY();
			GuiComponent.blit(matrixStack, x, y, 0, 141, 166, 24, 24, 256, 256);
			int color = player.getEntityData().get(LivingEntityAccess.getParameter());
			iconItem.getOrCreateTag().putInt("CustomPotionColor", color);
			minecraft.getItemRenderer().renderAndDecorateItem(iconItem, x + 3, y + 4);
			matrixStack.pushPose();
			matrixStack.translate(0, 0, 200);
			x += 22;
			y += 14;
			if (effects - bad > 0) {
				String s = Integer.toString(effects - bad);
				minecraft.font.drawShadow(matrixStack, s, x - minecraft.font.width(s), y, 16777215);
				y -= 10;
			}
			if (bad > 0) {
				String s = Integer.toString(bad);
				minecraft.font.drawShadow(matrixStack, s, x - minecraft.font.width(s), y, 16733525);
			}
			matrixStack.popPose();
			ci.cancel();
		}
	}

	private void updateArea() {
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
			return Collections.EMPTY_LIST;
		return Collections.singletonList(area);
	}

	@Override
	public boolean isExpanded() {
		return expand;
	}

	// cancel potion shift
	//	@Inject(at = @At("TAIL"), method = "checkEffectRendering")
	//	private void minieffects$checkEffectRendering(CallbackInfo ci) {
	//		if (!minecraft.player.getActiveEffects().isEmpty())
	//			this.leftPos = (this.width - this.imageWidth) / 2;
	//	}

	@Shadow
	abstract boolean canSeeEffects();
}
