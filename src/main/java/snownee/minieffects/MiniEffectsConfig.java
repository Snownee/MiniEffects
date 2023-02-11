package snownee.minieffects;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = "minieffects")
public final class MiniEffectsConfig {
	private MiniEffectsConfig() {
	}

	@SubscribeEvent
	public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MiniEffects.ID)) {
			ConfigManager.sync(MiniEffects.ID, Config.Type.INSTANCE);
		}
	}

	public static int xOffset;
	public static int yOffset;

}