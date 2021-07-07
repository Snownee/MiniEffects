package org.teacon.theelixir.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * @author DustW
 */
@Mixin(value = DisplayEffectsScreen.class)
public class MixinDisplayEffectsScreen {
    @Shadow protected boolean hasActivePotionEffects;
    DisplayEffectsScreen<PlayerContainer> THIS = (DisplayEffectsScreen<PlayerContainer>) (Object) this;
    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
    private static boolean expanding = false;

    @Inject(method = "renderEffects", cancellable = true, at = @At(value = "HEAD"))
    public void mineRenderEffects(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void mineRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (hasActivePotionEffects) {
            renderPotions(matrixStack, mouseX, mouseY);
        }
    }

    void renderPotions(MatrixStack matrixStack, int mouseX, int mouseY) {
        int i = THIS.getGuiLeft() - 124;
        Collection<EffectInstance> collection = THIS.getMinecraft().player.getActivePotionEffects();
        if (!collection.isEmpty()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = 33;
            if (collection.size() > 5) {
                j = 132 / (collection.size() - 1);
            }

            Iterable<EffectInstance> iterable = collection.stream().filter(effectInstance -> effectInstance.shouldRender()).sorted().collect(java.util.stream.Collectors.toList());

            int x = THIS.getGuiLeft() - 30;

            boolean inRangeA = mouseX >= x + 5 && mouseX <= x + 25 && mouseY >= THIS.getGuiTop() && mouseY <= THIS.getGuiTop() + 20;
            boolean inRangeB = mouseX >= i && mouseX <= THIS.getGuiLeft() && mouseY >= THIS.getGuiTop() && mouseY <= THIS.getGuiTop() + collection.size() * j;

            boolean open2 = expanding && inRangeB;

            if (inRangeA || open2) {
                renderEffectBackground(matrixStack, i, j, iterable);
                renderEffectSprites(matrixStack, i, j, iterable);
                renderEffectText(matrixStack, i, j, iterable);
                expanding = true;
            }
            else {
                ItemStack emptyPotion = new ItemStack(Items.POTION);
                PotionUtils.appendEffects(emptyPotion, collection);
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(emptyPotion, x + 9, THIS.getGuiTop() + 9);
                expanding = false;
            }
        }
    }

    private void renderEffectBackground(MatrixStack matrixStack, int renderX, int yOffset, Iterable<EffectInstance> effects) {
        THIS.getMinecraft().getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = THIS.getGuiTop();

        for(EffectInstance effectinstance : effects) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            THIS.blit(matrixStack, renderX, i, 0, 166, 140, 32);
            i += yOffset;
        }

    }

    private void renderEffectSprites(MatrixStack matrixStack, int renderX, int yOffset, Iterable<EffectInstance> effects) {
        PotionSpriteUploader potionspriteuploader = THIS.getMinecraft().getPotionSpriteUploader();
        int i = THIS.getGuiTop();

        for(EffectInstance effectinstance : effects) {
            Effect effect = effectinstance.getPotion();
            TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
            THIS.getMinecraft().getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            AbstractGui.blit(matrixStack, renderX + 6, i + 7, THIS.getBlitOffset(), 18, 18, textureatlassprite);
            i += yOffset;
        }

    }

    private void renderEffectText(MatrixStack matrixStack, int renderX, int yOffset, Iterable<EffectInstance> effects) {
        int i = THIS.getGuiTop();

        for(EffectInstance effectinstance : effects) {
            effectinstance.renderInventoryEffect(THIS, matrixStack, renderX, i, THIS.getBlitOffset());
            if (!effectinstance.shouldRenderInvText()) {
                i += yOffset;
                continue;
            }
            String s = I18n.format(effectinstance.getPotion().getName());
            if (effectinstance.getAmplifier() >= 1 && effectinstance.getAmplifier() <= 9) {
                s = s + ' ' + I18n.format("enchantment.level." + (effectinstance.getAmplifier() + 1));
            }

            FontRenderer font = THIS.getMinecraft().getRenderManager().getFontRenderer();

            font.drawStringWithShadow(matrixStack, s, (float)(renderX + 10 + 18), (float)(i + 6), 16777215);
            String s1 = EffectUtils.getPotionDurationString(effectinstance, 1.0F);
            font.drawStringWithShadow(matrixStack, s1, (float)(renderX + 10 + 18), (float)(i + 6 + 10), 8355711);
            i += yOffset;
        }

    }
}
