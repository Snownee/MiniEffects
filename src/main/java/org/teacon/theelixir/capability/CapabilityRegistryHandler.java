package org.teacon.theelixir.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistryHandler {
    @CapabilityInject(TheElixirCapability.class)
    public static Capability<TheElixirCapability> THE_ELIXIR_CAPABILITY;

    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CapabilityManager.INSTANCE.register(
                    TheElixirCapability.class,
                    new Capability.IStorage<TheElixirCapability>() {
                        @Nullable
                        @Override
                        public INBT writeNBT(Capability<TheElixirCapability> capability, TheElixirCapability instance, Direction side) { return null; }
                        @Override
                        public void readNBT(Capability<TheElixirCapability> capability, TheElixirCapability instance, Direction side, INBT nbt) { }
                    },
                    () -> null
            );
        });
    }
}
