package snownee.minieffects.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;

@Mixin(LivingEntity.class)
public interface MixinLivingEntity {
	@Accessor("POTION_EFFECTS")
	static DataParameter<Integer> getParameter() {
		throw new UnsupportedOperationException("wat?");
	}
}
