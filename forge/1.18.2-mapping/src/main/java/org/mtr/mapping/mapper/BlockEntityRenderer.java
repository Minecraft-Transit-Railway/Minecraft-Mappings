package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityRendererArgument;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T> {

	@MappedMethod
	public BlockEntityRenderer(BlockEntityRendererArgument argument) {
	}

	@Deprecated
	@Override
	public final void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		render(entity, tickDelta, new GraphicsHolder(matrices, vertexConsumers), light, overlay);
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);
}
