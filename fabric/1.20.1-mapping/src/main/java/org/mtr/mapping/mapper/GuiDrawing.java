package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private VertexConsumer vertexConsumer;
	private DrawContext drawContext;
	private BufferBuilder bufferBuilder;
	private final GraphicsHolder graphicsHolder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		this.graphicsHolder = graphicsHolder;
	}

	@MappedMethod
	public void beginDrawingRectangle() {
		vertexConsumer = graphicsHolder.vertexConsumerProvider == null ? null : graphicsHolder.vertexConsumerProvider.getBuffer(net.minecraft.client.render.RenderLayer.getGui());
		drawContext = graphicsHolder.drawContext;
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (vertexConsumer != null && drawContext != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				vertexConsumer.vertex(x1, y1, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(x1, y2, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(x2, y2, 0).color(r, g, b, a).next();
				vertexConsumer.vertex(x2, y1, 0).color(r, g, b, a).next();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		if (vertexConsumer != null && drawContext != null) {
			drawContext.draw();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShaderTexture(0, identifier.data);
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (bufferBuilder != null) {
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(x1, y1, 0).texture(u1, v2).next();
			bufferBuilder.vertex(x1, y2, 0).texture(u2, v2).next();
			bufferBuilder.vertex(x2, y2, 0).texture(u2, v1).next();
			bufferBuilder.vertex(x2, y1, 0).texture(u1, v1).next();
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
	}
}
