package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.ScreenAbstractMapping;
import org.mtr.mapping.holder.Text;

public class ScreenExtension extends ScreenAbstractMapping {

	@MappedMethod
	protected ScreenExtension() {
		this("");
	}

	@MappedMethod
	protected ScreenExtension(String title) {
		this(TextHelper.literal(title));
	}

	@MappedMethod
	protected ScreenExtension(MutableText title) {
		super(new Text(title.data));
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.matrixStack != null) {
			super.render2(graphicsHolder.matrixStack, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(matrices, null), mouseX, mouseY, delta);
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.matrixStack != null) {
			renderBackground2(graphicsHolder.matrixStack);
		}
	}

	@MappedMethod
	public final void addChild(ClickableWidget child) {
		addDrawableChild(child.data);
	}

	@MappedMethod
	public static void drawCenteredText(GraphicsHolder graphicsHolder, String text, int centerX, int y, int color) {
		if (graphicsHolder.matrixStack != null) {
			drawCenteredText(graphicsHolder.matrixStack, MinecraftClient.getInstance().textRenderer, text, centerX, y, color);
		}
	}

	@MappedMethod
	public static void drawCenteredText(GraphicsHolder graphicsHolder, MutableText text, int centerX, int y, int color) {
		if (graphicsHolder.matrixStack != null) {
			drawCenteredText(graphicsHolder.matrixStack, MinecraftClient.getInstance().textRenderer, text.data, centerX, y, color);
		}
	}
}
