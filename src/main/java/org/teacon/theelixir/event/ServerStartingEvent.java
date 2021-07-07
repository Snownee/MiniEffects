package org.teacon.theelixir.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.ServerLifecycleEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber
public class ServerStartingEvent {
    @SubscribeEvent
    public static void onServerSetup(FMLServerStartedEvent event) {
        ModServerTickEvent.players = event.getServer().getPlayerList().getPlayers();
    }
}
