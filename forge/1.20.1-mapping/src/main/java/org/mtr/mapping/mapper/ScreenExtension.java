package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
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
		if (graphicsHolder.guiGraphics != null) {
			super.render2(graphicsHolder.guiGraphics, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(guiGraphics), mouseX, mouseY, delta);
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.guiGraphics != null) {
			renderBackground2(graphicsHolder.guiGraphics);
		}
	}

	@MappedMethod
	public final void addChild(ClickableWidget child) {
		addRenderableWidget(child.data);
	}

	@MappedMethod
	public static void drawCenteredText(GraphicsHolder graphicsHolder, String text, int centerX, int y, int color) {
		if (graphicsHolder.guiGraphics != null) {
			graphicsHolder.guiGraphics.drawCenteredString(Minecraft.getInstance().font, text, centerX, y, color);
		}
	}

	@MappedMethod
	public static void drawCenteredText(GraphicsHolder graphicsHolder, MutableText text, int centerX, int y, int color) {
		if (graphicsHolder.guiGraphics != null) {
			graphicsHolder.guiGraphics.drawCenteredString(Minecraft.getInstance().font, text.data, centerX, y, color);
		}
	}
}
