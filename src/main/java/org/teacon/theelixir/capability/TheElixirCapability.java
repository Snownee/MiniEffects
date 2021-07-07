package org.teacon.theelixir.capability;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * @author DustW
 */
public class TheElixirCapability implements INBTSerializable<CompoundNBT> {
    private boolean usedElixir = false;
    /** 不要在客户端调用 */
    private ServerPlayerEntity owner;

    private BlockPos lastPos;
    private ServerWorld lastWorld;

    public int difficultyPoint;

    public void init(ServerPlayerEntity owner) {
        this.owner = owner;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT result = new CompoundNBT();
        result.putBoolean("used_elixir", usedElixir);
        result.putInt("difficultyPoint", difficultyPoint);
        return result;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        usedElixir = nbt.getBoolean("used_elixir");
        difficultyPoint = nbt.getInt("difficultyPoint");
    }

    public boolean isUsedElixir() {
        return usedElixir;
    }

    public void setUsedElixir(boolean usedElixir) {
        this.usedElixir = usedElixir;
    }

    public void tick() {
        if (isUsedElixir()) {
            owner.clearActivePotions();
            owner.extinguish();

            owner.getFoodStats().setFoodLevel(1);

            if (lastWorld == null) {
                lastWorld = owner.getServerWorld();
            }

            if (lastPos == null) {
                lastPos = owner.getPosition();
            }

            if (owner.getPosition().distanceSq(lastPos) > 1000 && lastWorld == owner.getServerWorld()) {
                owner.setPosition(lastPos.getX(), lastPos.getY(), lastPos.getZ());
            }
            else {
                lastPos = owner.getPosition();
                lastWorld = owner.getServerWorld();
            }
        }
    }
}
