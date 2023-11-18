package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;

public final class OptimizedRenderer {

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
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int light) {
		if (graphicsHolder.matrixStack != null) {
			batchManager.queue(optimizedModel.uploadedParts, new VertexAttributeState(light, Utilities.copy(new Matrix4f(graphicsHolder.matrixStack.last().pose()))));
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
}
