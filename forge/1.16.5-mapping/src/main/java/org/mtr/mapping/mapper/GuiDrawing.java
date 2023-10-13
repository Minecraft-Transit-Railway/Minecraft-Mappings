package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

public final class GuiDrawing extends DummyClass {

	private BufferBuilder bufferBuilder;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
	}

	@MappedMethod
	public void beginDrawingRectangle() {
		bufferBuilder = Tessellator.getInstance().getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (bufferBuilder != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				bufferBuilder.vertex(x1, y1, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x1, y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x2, y2, 0).color(r, g, b, a).endVertex();
				bufferBuilder.vertex(x2, y1, 0).color(r, g, b, a).endVertex();
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
		if (bufferBuilder != null) {
			bufferBuilder.vertex(x1, y1, 0).uv(u1, v1).endVertex();
			bufferBuilder.vertex(x1, y2, 0).uv(u1, v2).endVertex();
			bufferBuilder.vertex(x2, y2, 0).uv(u2, v2).endVertex();
			bufferBuilder.vertex(x2, y1, 0).uv(u2, v1).endVertex();
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
