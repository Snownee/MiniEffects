package org.teacon.theelixir;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupListener {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getItemColors().register((stack, color) ->
                color > 0 ? -1 :
                PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(stack)), Items.POTION));
    }
}
