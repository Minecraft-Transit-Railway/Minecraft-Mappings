package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GraphicsHolder extends DummyClass {

	VertexConsumer vertexConsumer;
	private int matrixPushes;

	final MatrixStack matrixStack;
	final VertexConsumerProvider vertexConsumerProvider;
	final DrawContext drawContext;

	@MappedMethod
	public static int getDefaultLight() {
		return 0xF000F0;
	}

	@Deprecated
	public static void createInstanceSafe(@Nullable MatrixStack matrixStack, @Nullable VertexConsumerProvider vertexConsumerProvider, Consumer<GraphicsHolder> consumer) {
		createInstanceSafe(new GraphicsHolder(matrixStack, vertexConsumerProvider), consumer);
	}

	@Deprecated
	public static void createInstanceSafe(DrawContext drawContext, Consumer<GraphicsHolder> consumer) {
		createInstanceSafe(new GraphicsHolder(drawContext), consumer);
	}

	private static void createInstanceSafe(GraphicsHolder graphicsHolder, Consumer<GraphicsHolder> consumer) {
		try {
			consumer.accept(graphicsHolder);
		} catch (Exception e) {
			logException(e);
		}

		while (graphicsHolder.matrixPushes > 0) {
			graphicsHolder.pop();
		}
	}

	private GraphicsHolder(@Nullable MatrixStack matrixStack, @Nullable VertexConsumerProvider vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		drawContext = null;
		push();
	}

	private GraphicsHolder(DrawContext drawContext) {
		this.matrixStack = drawContext.getMatrices();
		this.vertexConsumerProvider = drawContext.getVertexConsumers();
		this.drawContext = drawContext;
		push();
	}

	@MappedMethod
	public void push() {
		if (matrixStack != null) {
			matrixStack.push();
			matrixPushes++;
		}
	}

	@MappedMethod
	public void pop() {
		if (matrixStack != null && matrixPushes > 0) {
			matrixStack.pop();
			matrixPushes--;
		}
	}

	@MappedMethod
	public void translate(double x, double y, double z) {
		if (matrixStack != null) {
			matrixStack.translate(x, y, z);
		}
	}

	@MappedMethod
	public void scale(float x, float y, float z) {
		if (matrixStack != null) {
			matrixStack.scale(x, y, z);
		}
	}

	@MappedMethod
	public void rotateXRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateYRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateZRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateXDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateYDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateZDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void drawText(MutableText mutableText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final VertexConsumerProvider.Immediate immediate = drawContext == null ? VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()) : drawContext.getVertexConsumers();
			getInstance().textRenderer.draw(mutableText.data, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.draw();
			} else {
				drawContext.draw();
			}
		}
	}

	@MappedMethod
	public void drawText(OrderedText orderedText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final VertexConsumerProvider.Immediate immediate = drawContext == null ? VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()) : drawContext.getVertexConsumers();
			getInstance().textRenderer.draw(orderedText.data, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.draw();
			} else {
				drawContext.draw();
			}
		}
	}

	@MappedMethod
	public void drawText(String text, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null) {
			final VertexConsumerProvider.Immediate immediate = drawContext == null ? VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()) : drawContext.getVertexConsumers();
			getInstance().textRenderer.draw(text, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
			if (drawContext == null) {
				immediate.draw();
			} else {
				drawContext.draw();
			}
		}
	}

	@MappedMethod
	public void drawCenteredText(String text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.drawCenteredTextWithShadow(getInstance().textRenderer, text, centerX, y, color);
		}
	}

	@MappedMethod
	public void drawCenteredText(MutableText text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.drawCenteredTextWithShadow(getInstance().textRenderer, text.data, centerX, y, color);
		}
	}

	@MappedMethod
	public static int getTextWidth(MutableText mutableText) {
		return getInstance().textRenderer.getWidth(mutableText.data);
	}

	@MappedMethod
	public static int getTextWidth(OrderedText orderedText) {
		return getInstance().textRenderer.getWidth(orderedText.data);
	}

	@MappedMethod
	public static int getTextWidth(String text) {
		return getInstance().textRenderer.getWidth(text);
	}

	@MappedMethod
	public static List<OrderedText> wrapLines(MutableText mutableText, int width) {
		return getInstance().textRenderer.wrapLines(mutableText.data, width).stream().map(OrderedText::new).collect(Collectors.toList());
	}

	private static MinecraftClient getInstance() {
		return MinecraftClient.getInstance();
	}

	/**
	 * Always call before drawing lines or textures in the world.
	 */
	@MappedMethod
	public void createVertexConsumer(RenderLayer renderLayer) {
		if (vertexConsumerProvider != null) {
			vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer.data);
		}
	}

	/**
	 * Always call {@link GraphicsHolder#createVertexConsumer(RenderLayer)} beforehand.
	 */
	@MappedMethod
	public void drawLineInWorld(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				final MatrixStack.Entry entry = matrixStack.peek();
				final Matrix4f matrix4f = entry.getPositionMatrix();
				final Matrix3f matrix3f = entry.getNormalMatrix();

				vertexConsumer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(matrix3f, 0, 1, 0).next();
				vertexConsumer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(matrix3f, 0, 1, 0).next();
			});
		}
	}

	/**
	 * Always call {@link GraphicsHolder#createVertexConsumer(RenderLayer)} beforehand.
	 */
	@MappedMethod
	public void drawTextureInWorld(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				final Vector3i vector3i = facing.getVector();
				final int x = vector3i.getX();
				final int y = vector3i.getY();
				final int z = vector3i.getZ();

				final MatrixStack.Entry entry = matrixStack.peek();
				final Matrix4f matrix4f = entry.getPositionMatrix();
				final Matrix3f matrix3f = entry.getNormalMatrix();

				vertexConsumer.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).texture(u1, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, x, y, z).next();
				vertexConsumer.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).texture(u2, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, x, y, z).next();
				vertexConsumer.vertex(matrix4f, x3, y3, z3).color(r, g, b, a).texture(u2, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, x, y, z).next();
				vertexConsumer.vertex(matrix4f, x4, y4, z4).color(r, g, b, a).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, x, y, z).next();
			});
		}
	}

	@MappedMethod
	public void renderEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta, int light) {
		if (matrixStack != null && vertexConsumerProvider != null) {
			getInstance().getEntityRenderDispatcher().render(entity.data, x, y, z, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
		}
	}
}
