package snownee.minieffects;

import me.shedaniel.rei.api.client.config.ConfigObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;

@Mod("minieffects")
public class MiniEffects {

	public static boolean hasEffectsLeft;
	public static boolean hasREI;

	public MiniEffects() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		if (FMLEnvironment.dist.isClient()) {
			hasEffectsLeft = ModList.get().isLoaded("effectsleft");
			hasREI = ModList.get().isLoaded("roughlyenoughitems");
			MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onPotionSizeEvent);
		}
	}

	public static boolean isLeftSide() {
		if (hasREI && ConfigObject.getInstance().isLeftSideMobEffects()) {
			return true;
		}
		return hasEffectsLeft || MiniEffectsConfig.effectsOnLeft;
	}

	@OnlyIn(Dist.CLIENT)
	public void onPotionSizeEvent(ScreenEvent.RenderInventoryMobEffects event) {
		event.setCompact(event.getAvailableSpace() < 120);
	}

}