package org.teacon.theelixir.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.teacon.theelixir.TheElixir;

/**
 * @author DustW
 */
public class Restrainer extends SwordItem {
    public Restrainer() {
        super(new IItemTier() {
            @Override
            public int getMaxUses() {
                return 114514;
            }

            @Override
            public float getEfficiency() {
                return 12;
            }

            @Override
            public float getAttackDamage() {
                return 1919810;
            }

            @Override
            public int getHarvestLevel() {
                return 4;
            }

            @Override
            public int getEnchantability() {
                return 22;
            }

            @Override
            public Ingredient getRepairMaterial() {
                return Ingredient.fromItems(Items.PUMPKIN);
            }
        }, 0, TheElixir.THE_ULTIMATE_ANSWER_TO_THE_UNIVERSE, new Properties().group(ItemGroup.COMBAT));

        setRegistryName(new ResourceLocation(TheElixir.MOD_ID, "restrainer"));
    }
}
