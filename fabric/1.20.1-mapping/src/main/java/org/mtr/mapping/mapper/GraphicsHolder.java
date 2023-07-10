package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.OrderedText;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class GraphicsHolder extends DummyClass {

	private int matrixPushes;

	final MatrixStack matrixStack;
	final VertexConsumerProvider vertexConsumerProvider;
	final DrawContext guiGraphics;
	private final VertexConsumerProvider.Immediate immediate;

	public static final int DEFAULT_LIGHT = 0xF000F0;

	public GraphicsHolder(@Nullable MatrixStack matrixStack, @Nullable VertexConsumerProvider vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		guiGraphics = null;
		immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		push();
	}

	public GraphicsHolder(DrawContext guiGraphics) {
		this.matrixStack = guiGraphics.getMatrices();
		this.vertexConsumerProvider = null;
		this.guiGraphics = guiGraphics;
		immediate = guiGraphics.getVertexConsumers();
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
	public void popAll() {
		while (matrixPushes > 0) {
			pop();
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
		if (matrixStack != null && immediate != null) {
			getInstance().textRenderer.draw(mutableText.data, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
		}
	}

	@MappedMethod
	public void drawText(OrderedText orderedText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null && immediate != null) {
			getInstance().textRenderer.draw(orderedText.data, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
		}
	}

	@MappedMethod
	public void drawText(String text, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null && immediate != null) {
			getInstance().textRenderer.draw(text, x, y, color, shadow, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, light);
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

	@MappedMethod
	public void drawImmediate() {
		if (guiGraphics != null) {
			guiGraphics.draw();
		} else if (immediate != null) {
			immediate.draw();
		}
	}
}
