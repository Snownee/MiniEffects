package org.teacon.theelixir.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.teacon.theelixir.TheElixir;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * @author DustW
 */
public class SunroVientianeParaquatItem extends PotionItem {
    public SunroVientianeParaquatItem() {
        super(new Properties());
        setRegistryName(new ResourceLocation(TheElixir.MOD_ID, "sunro_vientiane_paraquat"));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        Set<Map.Entry<RegistryKey<Effect>, Effect>> potions = Registry.EFFECTS.getEntries();
        ArrayList<EffectInstance> effects = new ArrayList<>();

        for (Map.Entry<RegistryKey<Effect>, Effect> entry : potions) {
            effects.add(new EffectInstance(entry.getValue(), 100, 100));
        }

        PotionUtils.appendEffects(stack, effects);
        return super.onLeftClickEntity(stack, player, entity);
    }
}
