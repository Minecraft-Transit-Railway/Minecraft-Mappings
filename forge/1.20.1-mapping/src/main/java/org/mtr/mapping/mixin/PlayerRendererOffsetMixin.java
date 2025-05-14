package org.mtr.mapping.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.mapper.EntityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.entity.player.PlayerRenderer.class)
public abstract class PlayerRendererOffsetMixin {

	@Inject(method = "getRenderOffset(Lnet/minecraft/client/player/AbstractClientPlayer;F)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "RETURN"), cancellable = true)
	public void getRenderOffset(AbstractClientPlayer abstractClientPlayer, float f, CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
		if (EntityHelper.HIDDEN_PLAYERS.stream().anyMatch(uuid -> uuid.equals(abstractClientPlayer.getUUID()))) {
			callbackInfoReturnable.setReturnValue(new Vec3(0, -1000, 0));
		}
	}
}
