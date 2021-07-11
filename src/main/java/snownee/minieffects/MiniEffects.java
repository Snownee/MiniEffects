package snownee.minieffects;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("minieffects")
public class MiniEffects {
    public MiniEffects() {
        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(this::onPotionShift);
        }
    }

    public void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
        event.setCanceled(true);
    }

}
