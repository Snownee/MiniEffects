package snownee.minieffects;

import me.shedaniel.rei.api.client.config.ConfigObject;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("minieffects")
public class MiniEffects {

	public static boolean hasEffectsLeft;
	public static boolean hasREI;

	public MiniEffects(IEventBus modBus) {
		modBus.addListener((FMLClientSetupEvent event) -> onInitializeClient());
	}

	public void onInitializeClient() {
		hasEffectsLeft = ModList.get().isLoaded("effectsleft");
		hasREI = ModList.get().isLoaded("roughlyenoughitems");
	}

	public static boolean isLeftSide() {
		if (hasREI && ConfigObject.getInstance().isLeftSideMobEffects()) {
			return true;
		}
		return hasEffectsLeft || MiniEffectsConfig.effectsOnLeft;
	}

}
