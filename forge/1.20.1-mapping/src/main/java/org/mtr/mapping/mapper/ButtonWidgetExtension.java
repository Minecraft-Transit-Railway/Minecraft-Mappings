package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
		if (graphicsHolder.drawContext != null) {
			super.render2(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(guiGraphics), mouseX, mouseY, delta);
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
	public static void drawCenteredText(GraphicsHolder graphicsHolder, String text, int centerX, int y, int color) {
		if (graphicsHolder.drawContext != null) {
			graphicsHolder.drawContext.drawCenteredString(Minecraft.getInstance().font, text, centerX, y, color);
		}
	}

	@MappedMethod
	public static void drawCenteredText(GraphicsHolder graphicsHolder, MutableText text, int centerX, int y, int color) {
		if (graphicsHolder.drawContext != null) {
			graphicsHolder.drawContext.drawCenteredString(Minecraft.getInstance().font, text.data, centerX, y, color);
		}
	}
}
