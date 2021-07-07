package org.teacon.theelixir.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.teacon.theelixir.TheElixir;
import org.teacon.theelixir.capability.CapabilityRegistryHandler;

/**
 * @author DustW
 */
public class TextItem extends Item {
    public TextItem() {
        super(new Properties());

        this.setRegistryName(new ResourceLocation(TheElixir.MOD_ID, "test_item"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            playerIn.sendMessage(new StringTextComponent("尝试remove玩家"), Util.DUMMY_UUID);
            playerIn.remove();
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
