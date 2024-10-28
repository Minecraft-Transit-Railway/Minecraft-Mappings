package org.mtr.mapping.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface PlayerTeleportationStateAccessor {

	@Accessor("isChangingDimension")
	void setInTeleportationState(boolean inTeleportationState);
}
