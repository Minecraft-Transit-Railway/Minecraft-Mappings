package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.SliderWidgetAbstractMapping;
import org.mtr.mapping.holder.Text;

public abstract class SliderWidgetExtension extends SliderWidgetAbstractMapping {

	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height) {
		this(x, y, width, height, "");
	}

	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height, String message) {
		this(x, y, width, height, TextHelper.literal(message));
	}

	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height, MutableText message) {
		super(x, y, width, height, new Text(message.data), 0);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.matrixStack != null) {
			super.renderButton2(graphicsHolder.matrixStack, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void renderButton2(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(matrices, null, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@Deprecated
	@Override
	public final boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
		return mouseScrolled3(mouseX, mouseY, amount);
	}

	@MappedMethod
	public boolean mouseScrolled3(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled2(mouseX, mouseY, amount);
	}

	@MappedMethod
	public final int getX2() {
		return x;
	}

	@MappedMethod
	public final int getY2() {
		return y;
	}

	@MappedMethod
	public final void setX2(int x) {
		this.x = x;
	}

	@MappedMethod
	public final void setY2(int y) {
		this.y = y;
	}

	@MappedMethod
	public final void setHeight2(int height) {
		super.setHeight2(height);
	}

	@MappedMethod
	@Override
	public final boolean isHovered2() {
		return super.isHovered2();
	}
}
