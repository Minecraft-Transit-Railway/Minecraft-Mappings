package org.mtr.mapping.mapper;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;

public abstract class Screen extends net.minecraft.client.gui.screen.Screen {

	@MappedMethod
	public Screen() {
		super(Text.literal(""));
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.guiGraphics != null) {
			super.render(graphicsHolder.guiGraphics, mouseX, mouseY, delta);
		}
	}

	@Override
	public final void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		render(new GraphicsHolder(drawContext), mouseX, mouseY, delta);
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
	public void onClose() {
		super.close();
	}

	@Override
	public final void close() {
		onClose();
	}

	@MappedMethod
	@Override
	public boolean shouldPause() {
		return super.shouldPause();
	}
}
