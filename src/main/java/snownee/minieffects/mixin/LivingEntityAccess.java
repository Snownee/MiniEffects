package snownee.minieffects.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccess {
	@Accessor("DATA_EFFECT_COLOR_ID")
	static EntityDataAccessor<Integer> getParameter() {
		throw new UnsupportedOperationException("wat?");
	}
}
