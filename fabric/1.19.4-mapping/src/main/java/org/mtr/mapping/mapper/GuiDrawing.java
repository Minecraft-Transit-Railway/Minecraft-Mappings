package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
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
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.peek().getPositionMatrix();
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (matrix != null && bufferBuilder != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).color(r, g, b, a).next();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.peek().getPositionMatrix();
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (matrix != null && bufferBuilder != null) {
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).texture(u1, v1).next();
			bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).texture(u1, v2).next();
			bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).texture(u2, v2).next();
			bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).texture(u2, v1).next();
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
	}
}
