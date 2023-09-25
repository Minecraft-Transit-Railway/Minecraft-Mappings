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
	}

	@Deprecated
	@Override
	public final void renderWidget2(PoseStack matrices, int mouseX, int mouseY, float delta) {
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

	@Deprecated
	@Override
	protected final void updateWidgetNarration2(NarrationElementOutput arg0) {
	}
}
