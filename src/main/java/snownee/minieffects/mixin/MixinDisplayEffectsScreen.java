package snownee.minieffects.mixin;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.RenderProperties;
import snownee.minieffects.IAreasGetter;

@Mixin(EffectRenderingInventoryScreen.class)
public class MixinDisplayEffectsScreen implements IAreasGetter {
	private boolean expand;
	private int effects;
	private Rect2i area;
	private ItemStack iconItem = new ItemStack(Items.POTION);

	@Inject(method = "init", at = @At("TAIL"))
	protected void minimaleffects_init(CallbackInfo ci) {
		updateArea();
	}

	@Inject(method = "renderEffects", cancellable = true, at = @At("HEAD"))
	public void minimaleffects_renderEffects(PoseStack matrixStack, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		int effects = 0, bad = 0;
		for (MobEffectInstance effectInstance : mc.player.getActiveEffects())
			if (RenderProperties.getEffectRenderer(effectInstance).shouldRender(effectInstance)) {
				++effects;
				if (!effectInstance.getEffect().isBeneficial())
					++bad;
			}

		if (this.effects != effects) {
			this.effects = effects;
			if (effects == 0 || expand) {
				updateArea();
			}
		}
		int x = (int) (mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
		int y = (int) (mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
		boolean expand = area.contains(x, y);
		if (expand != this.expand) {
			this.expand = expand;
			updateArea();
		}
		if (effects > 0 && !expand) {
			RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			x = area.getX();
			y = area.getY();
			GuiComponent.blit(matrixStack, x, y, 0, 141, 166, 24, 24, 256, 256);
			LocalPlayer player = mc.player;
			int color = player.getEntityData().get(MixinLivingEntity.getParameter());
			iconItem.getOrCreateTag().putInt("CustomPotionColor", color);
			mc.getItemRenderer().renderAndDecorateItem(iconItem, x + 3, y + 4);
			matrixStack.pushPose();
			matrixStack.translate(0, 0, 200);
			x += 22;
			y += 14;
			if (effects - bad > 0) {
				String s = Integer.toString(effects - bad);
				mc.font.drawShadow(matrixStack, s, x - mc.font.width(s), y, 16777215);
				y -= 10;
			}
			if (bad > 0) {
				String s = Integer.toString(bad);
				mc.font.drawShadow(matrixStack, s, x - mc.font.width(s), y, 16733525);
			}
			matrixStack.popPose();
			ci.cancel();
		}
	}

	private void updateArea() {
		AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
		int left = screen.getGuiLeft();
		int top = screen.getGuiTop();
		if (expand) {
			int height = effects > 5 ? 165 : 33 * effects;
			area = new Rect2i(left - 124, top, 119, height);
		} else {
			area = new Rect2i(left - 25, top, 20, 20);
		}
	}

	@Override
	public List<Rect2i> getAreas() {
		if (area == null || effects == 0)
			return Collections.EMPTY_LIST;
		return Collections.singletonList(area);
	}

}
