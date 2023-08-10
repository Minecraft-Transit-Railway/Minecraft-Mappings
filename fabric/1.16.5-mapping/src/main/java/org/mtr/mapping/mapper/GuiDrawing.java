package org.mtr.mapping.mapper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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
		bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (bufferBuilder != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(x1, y1, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(x1, y2, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(x2, y2, 0).color(r, g, b, a).next();
				bufferBuilder.vertex(x2, y1, 0).color(r, g, b, a).next();
				bufferBuilder.end();
			});
		}
	}

	@MappedMethod
	public void finishDrawingRectangle() {
		if (bufferBuilder != null) {
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		bufferBuilder = Tessellator.getInstance().getBuffer();
		MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (bufferBuilder != null) {
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex(x1, y1, 0).texture(u1, v2).next();
			bufferBuilder.vertex(x1, y2, 0).texture(u2, v2).next();
			bufferBuilder.vertex(x2, y2, 0).texture(u2, v1).next();
			bufferBuilder.vertex(x2, y1, 0).texture(u1, v1).next();
			bufferBuilder.end();
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
		if (bufferBuilder != null) {
			RenderSystem.enableAlphaTest();
			BufferRenderer.draw(bufferBuilder);
		}
	}
}
