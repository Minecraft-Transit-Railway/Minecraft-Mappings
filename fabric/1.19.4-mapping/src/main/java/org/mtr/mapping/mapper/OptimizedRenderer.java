package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.shader.ModShaderHandler;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Supplier;

public final class OptimizedRenderer extends DummyClass {

	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();
	private int protectedDepth;
	private int reloadDepth;

	@MappedMethod
	public void beginReload() {
		if (reloadDepth++ == 0) {
			shaderManager.reloadShaders();
		}
		beginProtectedState();
	}

	@MappedMethod
	public void finishReload() {
		if (reloadDepth > 0) {
			reloadDepth--;
			finishProtectedState();
		}
	}

	@MappedMethod
	public void runWithProtectedState(Runnable runnable) {
		beginProtectedState();
		try {
			runnable.run();
		} finally {
			finishProtectedState();
		}
	}

	@MappedMethod
	public <T> T runWithProtectedState(Supplier<T> supplier) {
		beginProtectedState();
		try {
			return supplier.get();
		} finally {
			finishProtectedState();
		}
	}

	@MappedMethod
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
		if (graphicsHolder.matrixStack != null) {
			batchManager.queue(optimizedModel.uploadedParts, new VertexAttributeState(color, light, Utilities.copy(new Matrix4f(graphicsHolder.matrixStack.peek().getPositionMatrix()))));
		}
	}

	@MappedMethod
	public void render(boolean renderTranslucent) {
		if (shaderManager.isReady()) {
			runWithProtectedState(() -> batchManager.drawAll(shaderManager, renderTranslucent));
		}
	}

	@MappedMethod
	public static boolean renderingShadows() {
		return ModShaderHandler.renderingShadows();
	}

	/**
	 * @return {@code true} for 1.17+, {@code false} otherwise
	 */
	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return true;
	}

	private void beginProtectedState() {
		if (protectedDepth++ == 0) {
			GlStateTracker.capture();
		}
	}

	private void finishProtectedState() {
		if (protectedDepth > 0 && --protectedDepth == 0) {
			GlStateTracker.restore();
		}
	}
}
