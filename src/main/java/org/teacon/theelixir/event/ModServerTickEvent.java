package org.teacon.theelixir.event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.teacon.theelixir.capability.CapabilityRegistryHandler;
import org.teacon.theelixir.capability.TheElixirCapability;

import java.util.List;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber
public class ModServerTickEvent {
    public static List<ServerPlayerEntity> players;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        players.forEach(player -> player.getCapability(CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY).ifPresent(TheElixirCapability::tick));
    }
}
