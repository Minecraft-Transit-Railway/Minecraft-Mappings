package org.mtr.mapping.mapper;

import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.TexturedButtonWidgetAbstractMapping;

public class TexturedButtonWidgetExtension extends TexturedButtonWidgetAbstractMapping {

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, org.mtr.mapping.holder.PressAction onPress) {
		this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, onPress, "");
	}

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, org.mtr.mapping.holder.PressAction onPress, String message) {
		this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, onPress, TextHelper.literal(message));
	}

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, org.mtr.mapping.holder.PressAction onPress, MutableText message) {
		super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, onPress, new Text(message.data));
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
