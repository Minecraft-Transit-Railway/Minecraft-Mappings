package org.mtr.mapping.mapper;

import net.minecraft.client.gui.GuiGraphics;
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
		if (graphicsHolder.drawContext != null) {
			super.render2(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(guiGraphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.drawContext != null) {
			renderBackground2(graphicsHolder.drawContext, 0, 0, 0);
		}
	}

	@MappedMethod
	public final void addChild(ClickableWidget child) {
		addRenderableWidget(child.data);
	}

	@Deprecated
	@Override
	public final boolean mouseScrolled2(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return mouseScrolled3(mouseX, mouseY, verticalAmount);
	}

	@MappedMethod
	public boolean mouseScrolled3(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled2(mouseX, mouseY, 0, amount);
	}
}
