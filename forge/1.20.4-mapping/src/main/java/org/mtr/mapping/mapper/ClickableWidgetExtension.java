package org.mtr.mapping.mapper;

import net.minecraft.client.gui.GuiGraphics;
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
	}

	@Deprecated
	@Override
	public final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(guiGraphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@Deprecated
	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return mouseScrolled2(mouseX, mouseY, verticalAmount);
	}

	@MappedMethod
	public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, 0, amount);
	}

	@MappedMethod
	public final int getX2() {
		return super.getX();
	}

	@MappedMethod
	public final int getY2() {
		return super.getY();
	}

	@MappedMethod
	public final void setX2(int x) {
		super.setX(x);
	}

	@MappedMethod
	public final void setY2(int y) {
		super.setY(y);
	}

	@MappedMethod
	@Override
	public final boolean isHovered() {
		return super.isHovered();
	}

	@Deprecated
	@Override
	protected final void updateWidgetNarration(NarrationElementOutput arg0) {
	}
}
