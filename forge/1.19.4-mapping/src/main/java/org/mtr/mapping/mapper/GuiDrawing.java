package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private BufferBuilder bufferBuilder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
	}

	@MappedMethod
	public void beginDrawingRectangle() {
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (bufferBuilder != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				bufferBuilder.vertex(x1, y1, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x1, y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x2, y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x2, y1, 0).color(r, g, b, a).endVertex();
				BufferUploader.drawWithShader(bufferBuilder.end());
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		RenderSystem.disableBlend();
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (bufferBuilder != null) {
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			bufferBuilder.vertex(x1, y1, 0).uv(u1, v1).endVertex();
			bufferBuilder.vertex(x1, y2, 0).uv(u1, v2).endVertex();
			bufferBuilder.vertex(x2, y2, 0).uv(u2, v2).endVertex();
			bufferBuilder.vertex(x2, y1, 0).uv(u2, v1).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
	}
}
