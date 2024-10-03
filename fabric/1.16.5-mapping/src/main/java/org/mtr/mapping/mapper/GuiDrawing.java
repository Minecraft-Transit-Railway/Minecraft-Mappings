package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
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
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.peek().getModel();
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
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
		if (bufferBuilder != null) {
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.peek().getModel();
		bufferBuilder = Tessellator.getInstance().getBuffer();
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
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
		if (bufferBuilder != null) {
			bufferBuilder.end();
			RenderSystem.enableAlphaTest();
			BufferRenderer.draw(bufferBuilder);
		}
	}
}
