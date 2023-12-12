package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyInterface;

public interface ModelHelper extends DummyInterface {

	@MappedMethod
	void render(GraphicsHolder graphicsHolder, int light, int overlay, float red, float green, float blue, float alpha);

	@Deprecated
	default void render3(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		GraphicsHolder.createInstanceSafe(matrixStack, null, graphicsHolder -> {
			graphicsHolder.vertexConsumer = vertexConsumer;
			render(graphicsHolder, light, overlay, red, green, blue, alpha);
		});
	}
}
