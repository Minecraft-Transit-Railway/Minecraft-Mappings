package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.OrderedText;
import org.mtr.mapping.tool.Dummy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class GraphicsHolder extends Dummy {

	private int matrixPushes;

	final PoseStack matrixStack;
	final MultiBufferSource vertexConsumerProvider;
	private final MultiBufferSource.BufferSource immediate;

	public static final int DEFAULT_LIGHT = 0xF000F0;

	public GraphicsHolder(@Nullable PoseStack matrixStack, @Nullable MultiBufferSource vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		push();
	}

	@MappedMethod
	public void push() {
		if (matrixStack != null) {
			matrixStack.pushPose();
			matrixPushes++;
		}
	}

	@MappedMethod
	public void pop() {
		if (matrixStack != null && matrixPushes > 0) {
			matrixStack.popPose();
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
			matrixStack.mulPose(Vector3f.XP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateYRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Vector3f.YP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateZRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Vector3f.ZP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateXDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateYDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateZDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void drawText(MutableText mutableText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null && immediate != null) {
			getInstance().font.drawInBatch(mutableText.data, x, y, color, shadow, matrixStack.last().pose(), immediate, false, 0, light);
		}
	}

	@MappedMethod
	public void drawText(OrderedText orderedText, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null && immediate != null) {
			getInstance().font.drawInBatch(orderedText.data, x, y, color, shadow, matrixStack.last().pose(), immediate, false, 0, light);
		}
	}

	@MappedMethod
	public void drawText(String text, int x, int y, int color, boolean shadow, int light) {
		if (matrixStack != null && immediate != null) {
			getInstance().font.drawInBatch(text, x, y, color, shadow, matrixStack.last().pose(), immediate, false, 0, light);
		}
	}

	@MappedMethod
	public static int getTextWidth(MutableText mutableText) {
		return getInstance().font.width(mutableText.data);
	}

	@MappedMethod
	public static int getTextWidth(OrderedText orderedText) {
		return getInstance().font.width(orderedText.data);
	}

	@MappedMethod
	public static int getTextWidth(String text) {
		return getInstance().font.width(text);
	}

	@MappedMethod
	public static List<OrderedText> wrapLines(MutableText mutableText, int width) {
		return getInstance().font.split(mutableText.data, width).stream().map(OrderedText::new).collect(Collectors.toList());
	}

	private static Minecraft getInstance() {
		return Minecraft.getInstance();
	}

	@MappedMethod
	public void drawImmediate() {
		if (immediate != null) {
			immediate.endBatch();
		}
	}
}
