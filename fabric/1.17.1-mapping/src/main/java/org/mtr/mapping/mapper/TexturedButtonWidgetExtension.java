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
			super.render2(graphicsHolder.matrixStack, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render2(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(matrices, null), mouseX, mouseY, delta);
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
	@Override
	public final boolean isHovered2() {
		return super.isHovered2();
	}
}
