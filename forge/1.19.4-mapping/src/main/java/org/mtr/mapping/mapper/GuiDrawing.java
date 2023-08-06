package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public class GuiDrawing extends DummyClass {

	private final BufferBuilder bufferBuilder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		ColorHelper.unpackColor(color, (a, r, g, b) -> {
			bufferBuilder.vertex(x1, y1, 0).color(r, g, b, a).endVertex();
			bufferBuilder.vertex(x1, y2, 0).color(r, g, b, a).endVertex();
			bufferBuilder.vertex(x2, y2, 0).color(r, g, b, a).endVertex();
			bufferBuilder.vertex(x2, y1, 0).color(r, g, b, a).endVertex();
		});
	}

	@MappedMethod
	public void finishDrawing() {
		BufferUploader.drawWithShader(bufferBuilder.end());
		RenderSystem.disableBlend();
	}
}
