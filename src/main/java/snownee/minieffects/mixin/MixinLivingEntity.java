package snownee.minieffects.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;

@Mixin(EntityLivingBase.class)
public interface MixinLivingEntity {
	@Accessor("POTION_EFFECTS")
	static DataParameter<Integer> getParameter() {
		throw new UnsupportedOperationException("wat?");
	}
}
