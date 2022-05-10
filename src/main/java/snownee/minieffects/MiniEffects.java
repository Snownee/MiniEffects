package snownee.minieffects;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod("minieffects")
public class MiniEffects {

	public static boolean hasMod;

	public MiniEffects() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		hasMod = ModList.get().isLoaded("effectsleft");
		if (FMLEnvironment.dist.isClient()) {
			MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPotionSizeEvent);
		}
	}

	public static boolean isLeftSide() {
		return hasMod || MiniEffectsConfig.effectsOnLeft;
	}

	public void onPotionSizeEvent(ScreenEvent.PotionSizeEvent event) {
		event.setResult(Event.Result.DEFAULT);
	}

}