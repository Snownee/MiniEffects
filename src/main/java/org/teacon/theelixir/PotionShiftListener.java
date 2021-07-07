package org.teacon.theelixir;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber
public class PotionShiftListener {
    @SubscribeEvent
    public static void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
        event.setCanceled(true);
    }
}
