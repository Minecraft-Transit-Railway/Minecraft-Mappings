package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityRendererArgument;

public abstract class BlockEntityRenderer<T extends BlockEntity> extends TileEntityRenderer<T> {

	@MappedMethod
	public BlockEntityRenderer(BlockEntityRendererArgument argument) {
		super(argument.data);
	}

	@Override
	public final void render(T entity, float tickDelta, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, int overlay) {
		render(entity, tickDelta, new GraphicsHolder(matrices, vertexConsumers), light, overlay);
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);
}
