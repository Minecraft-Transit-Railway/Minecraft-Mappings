package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

public final class OptimizedRenderer extends DummyClass {

	@MappedMethod
	public void beginReload() {
	}

	@MappedMethod
	public void finishReload() {
	}

	@MappedMethod
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
	}

	@MappedMethod
	public void render(boolean renderTranslucent) {
	}

	/**
	 * @return {@code true} for 1.17+, {@code false} otherwise
	 */
	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return false;
	}
}
