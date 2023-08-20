package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClickableWidgetAbstractMapping;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Text;

public class ClickableWidgetExtension extends ClickableWidgetAbstractMapping {

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height) {
		this(x, y, width, height, "");
	}

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height, String message) {
		this(x, y, width, height, TextHelper.literal(message));
	}

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height, MutableText message) {
		super(x, y, width, height, new Text(message.data));
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
	public final boolean isHovered2() {
		return super.isHoveredOrFocused2();
	}

	@Deprecated
	@Override
	public final void updateNarration2(NarrationElementOutput arg0) {
	}
}
