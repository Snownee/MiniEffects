package snownee.minieffects.mixin;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import snownee.minieffects.IAreasGetter;

@Mixin(DisplayEffectsScreen.class)
public class MixinDisplayEffectsScreen implements IAreasGetter {
    private boolean expand;
    private int effects;
    private Rectangle2d area;
    private ItemStack iconItem = new ItemStack(Items.POTION);

    @Inject(method = "init", at = @At("TAIL"))
    protected void minimaleffects_init(CallbackInfo ci) {
        updateArea();
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "renderEffects", cancellable = true, at = @At("HEAD"))
    public void minimaleffects_renderEffects(MatrixStack matrixStack, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        int effects = 0, bad = 0;
        for (EffectInstance effectInstance : mc.player.getActivePotionEffects())
            if (effectInstance.shouldRender()) {
                ++effects;
                if (!effectInstance.getPotion().isBeneficial())
                    ++bad;
            }

        if (this.effects != effects) {
            this.effects = effects;
            if (effects == 0 || expand) {
                updateArea();
            }
        }
        int x = (int) (mc.mouseHelper.getMouseX() * mc.getMainWindow().getScaledWidth() / mc.getMainWindow().getWidth());
        int y = (int) (mc.mouseHelper.getMouseY() * mc.getMainWindow().getScaledHeight() / mc.getMainWindow().getHeight());
        boolean expand = area.contains(x, y);
        if (expand != this.expand) {
            this.expand = expand;
            updateArea();
        }
        if (effects > 0 && !expand) {
            mc.getTextureManager().bindTexture(ContainerScreen.INVENTORY_BACKGROUND);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            x = area.getX();
            y = area.getY();
            AbstractGui.blit(matrixStack, x, y, 0, 141, 166, 24, 24, 256, 256);
            ClientPlayerEntity player = Minecraft.getInstance().player;
            int color = player.getDataManager().get(MixinLivingEntity.getParameter());
            iconItem.getOrCreateTag().putInt("CustomPotionColor", color);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(iconItem, x + 3, y + 4);
            matrixStack.translate(0, 0, 200);
            x += 22;
            y += 14;
            if (effects - bad > 0) {
                String s = Integer.toString(effects - bad);
                mc.fontRenderer.drawStringWithShadow(matrixStack, s, x - mc.fontRenderer.getStringWidth(s), y, 16777215);
                y -= 10;
            }
            if (bad > 0) {
                String s = Integer.toString(bad);
                mc.fontRenderer.drawStringWithShadow(matrixStack, s, x - mc.fontRenderer.getStringWidth(s), y, 16733525);
            }
            ci.cancel();
        }
    }

    private void updateArea() {
        ContainerScreen<?> screen = (ContainerScreen<?>) (Object) this;
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        if (expand) {
            int height = effects > 5 ? 165 : 33 * effects;
            area = new Rectangle2d(left - 124, top, 119, height);
        } else {
            area = new Rectangle2d(left - 25, top, 20, 20);
        }
    }

    @Override
    public List<Rectangle2d> getAreas() {
        if (area == null || effects == 0)
            return Collections.EMPTY_LIST;
        return Collections.singletonList(area);
    }

}
