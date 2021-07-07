package org.teacon.theelixir.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.teacon.theelixir.capability.CapabilityRegistryHandler;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber
public class ElixirPotionApplicableEvent {
    @SubscribeEvent
    public static void onPotionApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            event.getEntityLiving().getCapability(CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY).ifPresent(theCap -> {
                if (theCap.isUsedElixir()) {
                    event.setResult(Event.Result.DENY);
                }
            });
        }
    }
}
