package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
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
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		bufferBuilder = Tessellator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
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
		if (bufferBuilder != null) {
			bufferBuilder.end();
			WorldVertexBufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		matrix = graphicsHolder.matrixStack == null ? null : graphicsHolder.matrixStack.last().pose();
		bufferBuilder = Tessellator.getInstance().getBuilder();
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
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
		if (bufferBuilder != null) {
			bufferBuilder.end();
			RenderSystem.enableAlphaTest();
			WorldVertexBufferUploader.end(bufferBuilder);
		}
	}
}
