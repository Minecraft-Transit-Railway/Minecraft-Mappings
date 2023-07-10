package org.mtr.mapping.mapper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class Screen extends net.minecraft.client.gui.screens.Screen {

	@MappedMethod
	public Screen() {
		super(Component.literal(""));
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.guiGraphics != null) {
			super.render(graphicsHolder.guiGraphics, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(guiGraphics), mouseX, mouseY, delta);
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		if (graphicsHolder.guiGraphics != null) {
			renderBackground(graphicsHolder.guiGraphics);
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
