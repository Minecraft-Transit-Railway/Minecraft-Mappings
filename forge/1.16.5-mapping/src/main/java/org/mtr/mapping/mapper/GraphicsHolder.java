package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public final class GraphicsHolder {

	public final MatrixStack matrixStack;
	public final IRenderTypeBuffer vertexConsumerProvider;

	public GraphicsHolder(MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
	}
}
