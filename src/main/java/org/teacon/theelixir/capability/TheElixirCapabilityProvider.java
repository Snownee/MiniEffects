package org.teacon.theelixir.capability;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author DustW
 */
public class TheElixirCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    TheElixirCapability capability;

    @Override
    public CompoundNBT serializeNBT() {
        return getOrCreateCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        getOrCreateCapability().deserializeNBT(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityRegistryHandler.THE_ELIXIR_CAPABILITY ?
                LazyOptional.of(this::getOrCreateCapability).cast() : LazyOptional.empty();
    }

    @Nonnull
    private TheElixirCapability getOrCreateCapability() {
        if (capability == null) {
            capability = new TheElixirCapability();
        }

        return capability;
    }

    public TheElixirCapabilityProvider init(ServerPlayerEntity owner) {
        getOrCreateCapability().init(owner);
        return this;
    }
}
