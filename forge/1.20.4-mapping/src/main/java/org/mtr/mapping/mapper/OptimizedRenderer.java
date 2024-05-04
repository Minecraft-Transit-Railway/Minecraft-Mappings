package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.mtr.mapping.tool.DummyClass;

public final class OptimizedRenderer extends DummyClass {

	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();

	@MappedMethod
	public void beginReload() {
		shaderManager.reloadShaders();
		GlStateTracker.capture();
	}

	@MappedMethod
	public void finishReload() {
		GlStateTracker.restore();
	}

	@MappedMethod
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
		if (graphicsHolder.matrixStack != null) {
			batchManager.queue(optimizedModel.uploadedParts, new VertexAttributeState(color, light, Utilities.copy(new Matrix4f(graphicsHolder.matrixStack.last().pose()))));
		}
	}

	@MappedMethod
	public void render(boolean renderTranslucent) {
		if (shaderManager.isReady()) {
			GlStateTracker.capture();
			batchManager.drawAll(shaderManager, renderTranslucent);
			GlStateTracker.restore();
		}
	}

	/**
	 * @return {@code true} for 1.17+, {@code false} otherwise
	 */
	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return true;
	}
}
