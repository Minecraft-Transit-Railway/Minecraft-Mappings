package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> extends net.minecraft.client.render.block.entity.BlockEntityRenderer<T> {

	@MappedMethod
	public BlockEntityRenderer(Argument argument) {
		super(argument.data);
	}

	@Deprecated
	@Override
	public final void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		GraphicsHolder.createInstanceSafe(matrices, vertexConsumers, graphicsHolder -> render(entity, tickDelta, graphicsHolder, light, overlay));
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);

	@MappedMethod
	public boolean rendersOutsideBoundingBox2(T blockEntity) {
		return super.rendersOutsideBoundingBox(blockEntity);
	}

	@Deprecated
	@Override
	public final boolean rendersOutsideBoundingBox(T blockEntity) {
		return rendersOutsideBoundingBox2(blockEntity);
	}

	@Deprecated
	public static final class Argument {

		private final BlockEntityRenderDispatcher data;

		public Argument(BlockEntityRenderDispatcher data) {
			this.data = data;
		}
	}
}
