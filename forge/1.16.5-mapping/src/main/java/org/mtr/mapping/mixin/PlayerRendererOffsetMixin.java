package org.mtr.mapping.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.math.vector.Vector3d;
import org.mtr.mapping.mapper.EntityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererOffsetMixin {

	@Inject(method = "getRenderOffset(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/vector/Vector3d;", at = @At(value = "RETURN"), cancellable = true)
	public void getRenderOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vector3d> callbackInfoReturnable) {
		if (EntityHelper.HIDDEN_PLAYERS.stream().anyMatch(uuid -> uuid.equals(abstractClientPlayerEntity.getUUID()))) {
			callbackInfoReturnable.setReturnValue(new Vector3d(0, -1000, 0));
		}
	}
}
