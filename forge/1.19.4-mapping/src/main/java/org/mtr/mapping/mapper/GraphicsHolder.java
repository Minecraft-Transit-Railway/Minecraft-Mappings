package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public final class GraphicsHolder {

	public final PoseStack matrixStack;
	public final MultiBufferSource vertexConsumerProvider;

	public GraphicsHolder(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
	}
}
