package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public class GuiDrawing extends DummyClass {

	private final BufferBuilder bufferBuilder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		ColorHelper.unpackColor(color, (a, r, g, b) -> {
			bufferBuilder.vertex(x1, y1, 0).color(r, g, b, a).next();
			bufferBuilder.vertex(x1, y2, 0).color(r, g, b, a).next();
			bufferBuilder.vertex(x2, y2, 0).color(r, g, b, a).next();
			bufferBuilder.vertex(x2, y1, 0).color(r, g, b, a).next();
		});
	}

	@MappedMethod
	public void finishDrawing() {
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}
}
