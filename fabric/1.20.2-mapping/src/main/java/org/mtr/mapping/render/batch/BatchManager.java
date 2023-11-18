package org.mtr.mapping.render.batch;

import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.util.*;

public final class BatchManager {

	private final Map<MaterialProperties, Set<RenderCall>> opaqueBatches = new HashMap<>();
	private final Map<MaterialProperties, Set<RenderCall>> cutoutBatches = new HashMap<>();
	private final Map<MaterialProperties, Set<RenderCall>> translucentBatches = new HashMap<>();

	public void queue(List<VertexArray> vertexArrays, VertexAttributeState vertexAttributeState) {
		vertexArrays.forEach(vertexArray -> queue(vertexArray, vertexAttributeState));
	}

	public void queue(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
		final MaterialProperties materialProperties = vertexArray.materialProperties;
		(materialProperties.translucent ? translucentBatches : materialProperties.cutoutHack ? cutoutBatches : opaqueBatches).computeIfAbsent(materialProperties, key -> new HashSet<>()).add(new RenderCall(vertexArray, vertexAttributeState));
	}

	public void drawAll(ShaderManager shaderManager, boolean renderTranslucent) {
		drawBatch(opaqueBatches, shaderManager);
		drawBatch(cutoutBatches, shaderManager);
		if (renderTranslucent) {
			drawBatch(translucentBatches, shaderManager);
		}
	}

	private static void drawBatch(Map<MaterialProperties, Set<RenderCall>> batches, ShaderManager shaderManager) {
		batches.forEach((materialProperties, renderCalls) -> {
			shaderManager.setupShaderBatchState(materialProperties);
			renderCalls.forEach(RenderCall::draw);
			shaderManager.cleanupShaderBatchState();
		});
		batches.clear();
	}

	private static class RenderCall {

		public final VertexArray vertexArray;
		public final VertexAttributeState vertexAttributeState;

		public RenderCall(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
			this.vertexArray = vertexArray;
			this.vertexAttributeState = vertexAttributeState;
		}

		public void draw() {
			vertexArray.bind();
			vertexAttributeState.apply();
			vertexArray.materialProperties.vertexAttributeState.apply();
			vertexArray.draw();
		}
	}
}
