package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityRendererArgument;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> extends net.minecraft.client.render.block.entity.BlockEntityRenderer<T> {

	@MappedMethod
	public BlockEntityRenderer(BlockEntityRendererArgument argument) {
		super(argument.data);
	}

	@Deprecated
	@Override
	public final void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		GraphicsHolder.createInstanceSafe(matrices, vertexConsumers, graphicsHolder -> render(entity, tickDelta, graphicsHolder, light, overlay));
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);
}
