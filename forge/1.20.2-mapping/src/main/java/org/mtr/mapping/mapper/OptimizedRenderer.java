package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.util.ArrayList;
import java.util.List;

public final class OptimizedRenderer {

	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();
	private final List<Runnable> queue = new ArrayList<>();

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
			final Matrix4f matrix4f = Utilities.copy(new Matrix4f(graphicsHolder.matrixStack.last().pose()));
			queue.add(() -> batchManager.queue(optimizedModel.uploadedOpaqueParts, new VertexAttributeState(light, matrix4f)));
		}
	}

	@MappedMethod
	public void render() {
		if (!queue.isEmpty()) {
			queue.forEach(Runnable::run);
			GlStateTracker.capture();
			batchManager.drawAll(shaderManager);
			GlStateTracker.restore();
		}
		queue.clear();
	}
}
