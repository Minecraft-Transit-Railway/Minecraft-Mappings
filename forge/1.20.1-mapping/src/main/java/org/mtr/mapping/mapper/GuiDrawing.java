package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private VertexConsumer vertexConsumer;
	private GuiGraphics drawContext;
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
		vertexConsumer = graphicsHolder.vertexConsumerProvider == null ? null : graphicsHolder.vertexConsumerProvider.getBuffer(RenderType.gui());
		drawContext = graphicsHolder.drawContext;
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (matrix != null && vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.vertex(matrix, (float)x1, (float)y1, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(matrix, (float)x1, (float)y2, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(matrix, (float)x2, (float)y2, 0).color(r, g, b, a).endVertex();
				vertexConsumer.vertex(matrix, (float)x2, (float)y1, 0).color(r, g, b, a).endVertex();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		if (vertexConsumer != null && drawContext != null) {
			drawContext.flush();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (matrix != null && bufferBuilder != null) {
			bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).uv(u1, v1).endVertex();
			bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0).uv(u1, v2).endVertex();
			bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0).uv(u2, v2).endVertex();
			bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0).uv(u2, v1).endVertex();
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
		BufferUploader.drawWithShader(bufferBuilder.end());
	}
}
