package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private VertexConsumer vertexConsumer;
	private DrawContext drawContext;
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
		vertexConsumer = graphicsHolder.vertexConsumerProvider == null ? null : graphicsHolder.vertexConsumerProvider.getBuffer(net.minecraft.client.render.RenderLayer.getGui());
		drawContext = graphicsHolder.drawContext;
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (matrix != null && vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.vertex(matrix, (float)x1, (float)y1, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(matrix, (float)x1, (float)y2, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(matrix, (float)x2, (float)y2, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(matrix, (float)x2, (float)y1, 0).color(r, g, b, a).next();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		if (matrix != null && vertexConsumer != null && drawContext != null) {
			drawContext.draw();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.peek().getPositionMatrix();
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (matrix != null && bufferBuilder != null) {
			bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).texture(u1, v1).next();
			bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).texture(u1, v2).next();
			bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).texture(u2, v2).next();
			bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).texture(u2, v1).next();
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}
}
