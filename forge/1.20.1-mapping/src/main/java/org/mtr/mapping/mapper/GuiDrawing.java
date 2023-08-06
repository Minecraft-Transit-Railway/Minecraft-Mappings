package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public class GuiDrawing extends DummyClass {

	private final VertexConsumer vertexConsumer;
	private final GuiGraphics drawContext;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		vertexConsumer = graphicsHolder.vertexConsumerProvider == null ? null : graphicsHolder.vertexConsumerProvider.getBuffer(RenderType.gui());
		drawContext = graphicsHolder.drawContext;
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.vertex(x1, y1, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(x1, y2, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(x2, y2, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(x2, y1, 0).color(r, g, b, a).endVertex();
			});
		}
	}

	@MappedMethod
	public void finishDrawing() {
		if (vertexConsumer != null && drawContext != null) {
			drawContext.flush();
		}
	}
}
