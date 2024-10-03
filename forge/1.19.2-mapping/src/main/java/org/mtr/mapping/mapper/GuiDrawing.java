package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private BufferBuilder bufferBuilder;
	private Matrix4f matrix;
	private final GraphicsHolder graphicsHolder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		this.graphicsHolder = graphicsHolder;
	}

	@MappedMethod
	public void beginDrawingRectangle() {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (matrix != null && bufferBuilder != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).color(r, g, b, a).endVertex();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		BufferUploader.drawWithShader(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (matrix != null && bufferBuilder != null) {
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).uv(u1, v1).endVertex();
			bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).uv(u1, v2).endVertex();
			bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).uv(u2, v2).endVertex();
			bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).uv(u2, v1).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
	}
}
