package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public final class GraphicsHolder {

	public final MatrixStack matrixStack;
	public final VertexConsumerProvider vertexConsumerProvider;

	public GraphicsHolder(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
	}
}
