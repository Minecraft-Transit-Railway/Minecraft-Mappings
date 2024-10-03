package org.mtr.mapping.mapper;

import net.minecraft.client.gui.DrawContext;
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
			super.render(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(context, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.drawContext != null) {
			super.renderBackground(graphicsHolder.drawContext);
		}
	}

	@Deprecated
	public final void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
	}

	@MappedMethod
	public final void addChild(ClickableWidget child) {
		addDrawableChild(child.data);
	}

	@MappedMethod
	public final void addSelectableChild(ClickableWidget child) {
		addSelectableChild(child.data);
	}

	@Deprecated
	public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return mouseScrolled2(mouseX, mouseY, verticalAmount);
	}

	@MappedMethod
	public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}
