package org.teacon.theelixir.capability;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.teacon.theelixir.TheElixir;

/**
 * @author q2437
 */
@Mod.EventBusSubscriber
public class CommonCapabilityEvent {
    @SubscribeEvent
    public static void onAttachEntityCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            if (entity instanceof ServerPlayerEntity) {
                event.addCapability(new ResourceLocation(TheElixir.MOD_ID, "the_elixir_capability"), new TheElixirCapabilityProvider().init((ServerPlayerEntity) entity));
            } else if (entity instanceof ClientPlayerEntity) {
                event.addCapability(new ResourceLocation(TheElixir.MOD_ID, "the_elixir_capability"), new TheElixirCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            PlayerEntity original = event.getOriginal();
            PlayerEntity target = event.getPlayer();

            original.getCapability(CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY).ifPresent(originalCap -> {
                target.getCapability(CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY).ifPresent(targetCap -> {
                    targetCap.deserializeNBT(originalCap.serializeNBT());
                });
            });
        }
    }
}
