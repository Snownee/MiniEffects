package snownee.minieffects;

import me.shedaniel.rei.api.client.config.ConfigObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import snownee.kiwi.Mod;

@Mod("minieffects")
public class MiniEffects implements ClientModInitializer {

	public static boolean hasEffectsLeft;
	public static boolean hasREI;

	@Override
	public void onInitializeClient() {
		hasEffectsLeft = FabricLoader.getInstance().isModLoaded("effectsleft");
		hasREI = FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
	}

	public static boolean isLeftSide() {
		if (hasREI && ConfigObject.getInstance().isLeftSideMobEffects()) {
			return true;
		}
		return hasEffectsLeft || MiniEffectsConfig.effectsOnLeft;
	}

}
