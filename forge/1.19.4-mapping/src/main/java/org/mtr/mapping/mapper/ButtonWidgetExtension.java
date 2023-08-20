package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ButtonWidgetAbstractMapping;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Text;

import java.util.function.Supplier;

public class ButtonWidgetExtension extends ButtonWidgetAbstractMapping {

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, org.mtr.mapping.holder.PressAction onPress) {
		this(x, y, width, height, "", onPress);
	}

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, String message, org.mtr.mapping.holder.PressAction onPress) {
		this(x, y, width, height, TextHelper.literal(message), onPress);
	}

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, MutableText message, org.mtr.mapping.holder.PressAction onPress) {
		super(x, y, width, height, new Text(message.data), onPress, Supplier::get);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.matrixStack != null) {
			super.render2(graphicsHolder.matrixStack, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(PoseStack matrices, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(matrices, null, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@MappedMethod
	@Override
	public final int getX2() {
		return super.getX2();
	}

	@MappedMethod
	@Override
	public final int getY2() {
		return super.getY2();
	}

	@MappedMethod
	@Override
	public final void setX2(int x) {
		super.setX2(x);
	}

	@MappedMethod
	@Override
	public final void setY2(int y) {
		super.setY2(y);
	}

	@MappedMethod
	@Override
	public final boolean isHovered2() {
		return super.isHovered2();
	}
}
