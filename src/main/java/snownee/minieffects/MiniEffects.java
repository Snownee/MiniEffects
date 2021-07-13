package snownee.minieffects;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("minieffects")
public class MiniEffects {
	public MiniEffects() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "anything. i don't care", // if i'm actually on the server, this string is sent but i'm a client only mod, so it won't be
				(remoteversionstring, networkbool) -> networkbool) // i accept anything from the server, by returning true if it's asking about the server
		);
		if (FMLEnvironment.dist.isClient()) {
			MinecraftForge.EVENT_BUS.addListener(this::onPotionShift);
		}
	}

	public void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
		event.setCanceled(true);
	}

}
