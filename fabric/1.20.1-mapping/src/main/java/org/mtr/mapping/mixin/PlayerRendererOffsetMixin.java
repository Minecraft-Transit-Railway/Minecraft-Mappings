package org.mtr.mapping.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.Vec3d;
import org.mtr.mapping.mapper.EntityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererOffsetMixin {

	@Inject(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "RETURN"), cancellable = true)
	public void getRenderOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> callbackInfoReturnable) {
		if (EntityHelper.HIDDEN_PLAYERS.stream().anyMatch(uuid -> uuid.equals(abstractClientPlayerEntity.getUuid()))) {
			callbackInfoReturnable.setReturnValue(new Vec3d(0, -1000, 0));
		}
	}
}
