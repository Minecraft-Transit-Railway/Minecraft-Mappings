package org.mtr.mapping.mapper;

import net.minecraft.client.gui.DrawContext;
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
		if (graphicsHolder.drawContext != null) {
			super.render2(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(drawContext, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
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
	public final void setHeight2(int height) {
		this.height = height;
	}

	@MappedMethod
	@Override
	public final boolean isHovered2() {
		return super.isHovered2();
	}
}
