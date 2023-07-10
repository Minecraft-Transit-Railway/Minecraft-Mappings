package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.StringTextComponent;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class Screen extends net.minecraft.client.gui.screen.Screen {

	@MappedMethod
	public Screen() {
		super(StringTextComponent.EMPTY);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.matrixStack != null) {
			super.render(graphicsHolder.matrixStack, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(matrices, null), mouseX, mouseY, delta);
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.matrixStack != null) {
			renderBackground(graphicsHolder.matrixStack);
		}
	}

	@MappedMethod
	@Override
	public void init() {
		super.init();
	}

	@MappedMethod
	@Override
	public void tick() {
		super.tick();
	}

	@MappedMethod
	@Override
	public void onClose() {
		super.onClose();
	}

	@MappedMethod
	public boolean shouldPause() {
		return super.isPauseScreen();
	}

	@Deprecated
	@Override
	public final boolean isPauseScreen() {
		return shouldPause();
	}
}
